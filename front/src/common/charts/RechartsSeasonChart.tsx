import { type JSX } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend, ReferenceLine, TooltipProps, DefaultLegendContent } from 'recharts';
import { Box, Typography } from '@mui/material';
import '../Formatters.css';
import { ratingFormatter, stringSalaryFormatter } from '../Formatters';
import { NameType, Payload, ValueType } from 'recharts/types/component/DefaultTooltipContent';

export type TimeSeries = { season: number; round: number };

export interface ChartFormat {
    type: 'number' | 'currency' | 'rating'; // Type of formatting to apply
    divideBy?: number; // Value to divide by (e.g. 10 for ratings)
    decimals?: number; // Number of decimal places
    showCurrency?: boolean; // Whether to show currency symbol
}

export interface ChartDataProps<T extends TimeSeries> {
    title?: string;
    emptyMessage?: string;
    currencyName?: string;
    currencyRate?: number;
    seasonOffset: number;
    legendPosition: 'bottom' | 'right';
    showXAxisRounds?: boolean;
    format?: {
        type: 'number' | 'currency' | 'rating'; // Type of formatting to apply
        divideBy?: number; // Value to divide by (e.g. 10 for ratings)
        decimals?: number; // Number of decimal places
        showCurrency?: boolean; // Whether to show currency symbol
    };
    fieldConfig: Array<{
        fieldFunction: (data: T) => number | undefined; // Field from TeamRanking to display
        label: string; // Custom label for the legend
        color: string;  // Line color
        strokeWidth?: number; // Line thickness
    }>;
}


const VerticalLabel = (props: any) => {
    const x = props.props.viewBox.x;
    const y = props.props.viewBox.y;
    const height = props.props.viewBox.height;
    return (
        <text
            x={props.props.viewBox.x}
            y={(y + height / 2)}
            dy={-10}
            textAnchor="end"
            transform={`rotate(-90, ${x + 7}, ${y + height / 2})`}
            fontSize={12}
            fill="red"
        >
            Season: {props.props.season}
        </text>
    );
};


const formatValue = <T extends TimeSeries>(value: number, config?: ChartDataProps<T>['format'], currencyRate?: number, currencyName?: string): string => {
    if (!config) {
        return value.toLocaleString();
    }

    const formattedValue = config.divideBy ? value / config.divideBy : value;

    switch (config.type) {
        case 'currency':
            {
                let result = stringSalaryFormatter(formattedValue, currencyRate);
                if (config.showCurrency && currencyName) {
                    result += ` ${currencyName}`;
                }
                return result;
            }

        case 'rating':
            return formattedValue.toFixed(config.decimals || 1);
        case 'number':
        default:
            return formattedValue.toLocaleString(undefined, {
                minimumFractionDigits: config.decimals || 0,
                maximumFractionDigits: config.decimals || 0
            });
    }
};


const RechartsSeasonChart = <T extends TimeSeries>({
    chartData,
    title,
    format,
    emptyMessage = 'No chart data available',
    currencyName = '',
    currencyRate = 1,
    fieldConfig,
    seasonOffset,
    legendPosition,
    showXAxisRounds
}: ChartDataProps<T> & { chartData: T[] }) => {
    // Handle empty data case
    if (!chartData || chartData.length === 0) {
        return (
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%', py: 5 }}>
                <Typography variant="h6" color="textSecondary" sx={{ mb: 2.5 }}>
                    {title} (No Data)
                </Typography>
                <Typography variant="body2" color="textSecondary" sx={{ textAlign: 'center' }}>
                    {emptyMessage}
                </Typography>
            </Box>
        );
    }
    const chartDataCopy = chartData.map(cd => { return { ...cd, season: cd.season + seasonOffset } });
    if (chartDataCopy.length > 0) {
        chartDataCopy.sort((a, b) => {
            if (a.season !== b.season) {
                return a.season - b.season;
            }
            return a.round - b.round;
        });
    }

    // Calculate min and max values for Y-axis across all visible fields
    const allValues: number[] = [];
    fieldConfig.forEach(config => {
        chartDataCopy.forEach(item => {
            const val = config.fieldFunction(item);
            if (typeof val === 'number' && !isNaN(val)) {
                allValues.push(val);
            }
        });
    });

    const minValue = Math.min(...allValues);
    const maxValue = Math.max(...allValues);
    const minY = Math.floor(minValue * 0.8);
    const maxY = Math.floor(maxValue * 1.2);

    // Create reference lines for new seasons
    const referenceLines: JSX.Element[] = [];
    chartDataCopy.forEach((cd, index) => {
        if (cd.round === 1 && index !== 0 && chartDataCopy[index - 1].season !== cd.season) {
            referenceLines.push(
                <ReferenceLine
                    key={`ref-${cd.season}-${cd.round}`}
                    strokeDasharray="3 3"
                    x={cd.season.toString() + " " + cd.round.toString()}
                    stroke="red"
                    label={(props) => <VerticalLabel props={{ ...props, season: cd.season }} />}
                />
            );
        }
    });

    const renderCustomAxisTick = (props: any) => {
        const { x, y, payload } = props;
        const round = payload.value.split(' ')[1];
        return showXAxisRounds ? <text x={x} y={y + 15} textAnchor="middle" fill="#666" className={"dist_" + payload.index}>{round}</text> : <></>;
    };

    const renderCustomYAxisTick = (props: any) => {
        const { x, y, payload } = props;

        // Find a representative field to determine if this is a rating chart
        const isRatingChart = format?.type === 'rating';

        if (isRatingChart) {
            return (
                <g transform={`translate(${x},${y})`}>
                    <image
                        href="/star.svg"
                        height="16"
                        width="16"
                        x="-55"
                        y="-8"
                    />
                    <text
                        x="-30"
                        y="4"
                        textAnchor="start"
                        fill="#666"
                        style={{ fontSize: '12px' }}
                    >
                        x{(payload.value / 10).toFixed(1)}
                    </text>
                </g>
            );
        }

        return (
            <text x={x} y={y} dy={4} textAnchor="end" fill="#666">
                {formatYAxis(payload.value)}
            </text>
        );
    };

    const formatYAxis = (value: number) => {
        if (!format) return value.toString();

        return formatValue(value, format, currencyRate, currencyName);
    };

    const CustomTooltip = <Data extends TimeSeries>(props: TooltipProps<ValueType, NameType> & { payload: Array<Payload<ValueType, NameType>> }) => {
        if (props.active) {
            const payload = props.payload[0].payload as Data;
            const season = payload.season;
            const round = payload.round;
            return <Box sx={{ bgcolor: 'rgba(255, 255, 255, 0.9)', p: 1.5, border: '1px solid #ccc', borderRadius: 1 }}>
                <Typography variant="subtitle2" sx={{ mb: 1 }}>
                    Season {season}, Round {round}
                </Typography>
                {props.payload
                    .sort((a, b) => {
                        const aValue = a.value as number;
                        const bValue = b.value as number;
                        return (bValue || 0) - (aValue || 0);
                    })
                    .map((entry: any) => {
                        // Find the field config for this entry 
                        // payload
                        const typed = entry as { value: number | undefined, payload: Data }
                        const isRating = format?.type === 'rating'
                        return (
                            <Box key={entry.name} sx={{ display: 'flex', alignItems: 'center', mb: 0.5 }}>
                                <Box sx={{ width: 12, height: 12, bgcolor: entry.color, mr: 1 }} />
                                <Typography variant="body2" sx={{ mr: 1 }}>
                                    {entry.name}:
                                </Typography>
                                <Typography variant="body2" fontWeight="bold" sx={{ display: 'flex', alignItems: 'center' }}>
                                    {isRating ? (ratingFormatter(typed.value)) : (
                                        formatValue(
                                            entry.value,
                                            format,
                                            currencyRate,
                                            currencyName
                                        )
                                    )}
                                </Typography>
                            </Box>
                        );
                    })
                }

            </Box>
        }
        return null;
    }

    // Custom legend to reverse the order of items
    const renderLegend = (props: any) => {
        const reversedPayload = props.payload ? [...props.payload].reverse() : [];
        const newProps = { ...props, payload: reversedPayload };
        return (
            // ul here will help to make left margin
            <ul> {<DefaultLegendContent {...newProps} />} </ul>
        );
    }

    const legend = legendPosition === 'bottom' ?
        <Legend wrapperStyle={{ marginRight: 0, marginLeft: 20 }} />
        : <Legend content={renderLegend} align="right" verticalAlign='middle' layout="vertical" wrapperStyle={{ marginRight: 0, marginLeft: 20 }} />

    const minSeason = Math.min(...chartData.map(cd => cd.season));
    const maxSeason = Math.max(...chartData.map(cd => cd.season));
    return (
        <Box sx={{ width: '100%', height: 430 }}>
            <Typography variant="h6" sx={{ mb: 1, textAlign: 'center' }}>
                {title}
            </Typography>
            <ResponsiveContainer width="100%" height="100%">
                <LineChart
                    width={1500}
                    height={300}
                    data={chartDataCopy}
                    margin={{
                        top: 5,
                        right: 30,
                        left: 40,
                        bottom: 25,
                    }}
                >
                    <YAxis
                        width={80}
                        domain={[minY, maxY]}
                        tick={renderCustomYAxisTick}
                    />
                    <XAxis dataKey={(data: T) => { return data.season.toString() + " " + data.round.toString() }} tick={renderCustomAxisTick} />
                    <Tooltip content={CustomTooltip} />
                    <CartesianGrid strokeDasharray="1 9" />
                    {fieldConfig.map((config, index) => (
                        <Line
                            key={"ChartLine_" + index}
                            type="monotone"
                            dataKey={(data: T) => { return config.fieldFunction(data) }}
                            name={config.label}
                            stroke={config.color}
                            activeDot={{ r: 8 }}
                            dot={maxSeason - minSeason <= 3}
                            strokeWidth={config.strokeWidth || 1}
                        />
                    ))}
                    {referenceLines}
                    {legend}
                </LineChart>
            </ResponsiveContainer>
        </Box>
    );
}

export default RechartsSeasonChart;
