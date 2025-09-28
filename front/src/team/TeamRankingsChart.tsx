import React, { type JSX } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend, ReferenceLine } from 'recharts';
import type TeamRanking from '../rest/models/team/TeamRanking';
import { Box, Typography } from '@mui/material';
import '../common/Formatters.css';
import { ratingFormatter, stringSalaryFormatter } from '../common/Formatters';

export interface TeamRatingsChartProps {
    chartData: Array<TeamRanking>;
    title?: string;
    emptyMessage?: string;
    currencyName?: string;
    currencyRate?: number;
    fieldConfig: Array<{
        field: keyof TeamRanking; // Field from TeamRanking to display
        label?: string; // Custom label for the legend
        color: string;  // Line color
        strokeWidth?: number; // Line thickness
        format?: {
            type: 'number' | 'currency' | 'ratio'; // Type of formatting to apply
            divideBy?: number; // Value to divide by (e.g. 10 for ratings)
            decimals?: number; // Number of decimal places
            showCurrency?: boolean; // Whether to show currency symbol
        };
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


const formatValue = (value: number, config?: TeamRatingsChartProps['fieldConfig'][0]['format'], currencyRate?: number, currencyName?: string): string => {
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

        case 'ratio':
            return formattedValue.toFixed(config.decimals || 1);
        case 'number':
        default:
            return formattedValue.toLocaleString(undefined, {
                minimumFractionDigits: config.decimals || 0,
                maximumFractionDigits: config.decimals || 0
            });
    }
};

// Custom tooltip formatter to show more meaningful information
const CustomTooltip = ({ active, payload, label, fieldConfig, currencyName, currencyRate }: any) => {
    if (active && payload && payload.length) {
        const seasonRound = label.split(' ');
        return (
            <Box sx={{ bgcolor: 'rgba(255, 255, 255, 0.9)', p: 1.5, border: '1px solid #ccc', borderRadius: 1 }}>
                <Typography variant="subtitle2" sx={{ mb: 1 }}>
                    Season {seasonRound[0]}, Round {seasonRound[1]}
                </Typography>
                {payload.map((entry: any) => {
                    // Find the field config for this entry
                    const config = fieldConfig ?
                        fieldConfig.find((fc: any) => fc.field.toString() === entry.dataKey) : null;

                    // Check if this is a rating field that should show stars
                    const isRating = config?.format?.type === 'ratio' &&
                        (entry.dataKey === 'rating' || entry.dataKey === 'ratingEndOfMatch');

                    return (
                        <Box key={entry.name} sx={{ display: 'flex', alignItems: 'center', mb: 0.5 }}>
                            <Box sx={{ width: 12, height: 12, bgcolor: entry.color, mr: 1 }} />
                            <Typography variant="body2" sx={{ mr: 1 }}>
                                {entry.name}:
                            </Typography>
                            <Typography variant="body2" fontWeight="bold" sx={{ display: 'flex', alignItems: 'center' }}>
                                {isRating ? (ratingFormatter(entry.value)) : (
                                    formatValue(
                                        entry.value,
                                        config?.format,
                                        currencyRate,
                                        currencyName
                                    )
                                )}
                            </Typography>
                        </Box>
                    );
                })}
            </Box>
        );
    }
    return null;
};


const TeamRatingsChart: React.FC<TeamRatingsChartProps> = ({
    chartData,
    title,
    emptyMessage = 'No chart data available',
    currencyName = '',
    currencyRate = 1,
    fieldConfig
}) => {
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

    const formattedData = chartData.map(tr => {
        const dataPoint: any = {
            name: tr.season.toString() + " " + tr.round.toString(),
            season: tr.season,
            round: tr.round
        };

        fieldConfig.forEach(config => {
            dataPoint[config.field.toString()] = tr[config.field] as number;
        });

        return dataPoint;
    });

    formattedData.sort((a, b) => {
        if (a.season !== b.season) {
            return a.season - b.season;
        }
        return a.round - b.round;
    });

    // Calculate min and max values for Y-axis across all visible fields
    const allValues: number[] = [];
    fieldConfig.forEach(config => {
            chartData.forEach(item => {
                const val = item[config.field] as number;
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
    formattedData.forEach((cd, index) => {
        if (cd.round === 1 && index !== 0 && formattedData[index - 1].season !== cd.season) {
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
        // const round = (payload.index % modulo === 0) ? payload.value.split(' ')[1] : '';
        // for now disabling the round ticks
        return <text x={x} y={y + 15} textAnchor="middle" fill="#666" className={"dist_" + payload.index}></text>;
    };

    const renderCustomYAxisTick = (props: any) => {
        const { x, y, payload } = props;

        // Find a representative field to determine if this is a rating chart
        const firstFieldConfig = fieldConfig[0];
        const isRatingChart = firstFieldConfig?.format?.type === 'ratio' &&
            (firstFieldConfig?.field === 'rating' || firstFieldConfig?.field === 'ratingEndOfMatch');

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
        const firstFieldConfig = fieldConfig[0];
        if (!firstFieldConfig) return value.toString();

        return formatValue(value, firstFieldConfig.format, currencyRate, currencyName);
    };

    return (
        <Box sx={{ width: '100%', height: 430 }}>
            <Typography variant="h6" sx={{ mb: 1, textAlign: 'center' }}>
                {title}
            </Typography>
            <ResponsiveContainer width="100%" height="100%">
                <LineChart
                    width={1500}
                    height={300}
                    data={formattedData}
                    margin={{
                        top: 5,
                        right: 30,
                        left: 40,
                        bottom: 25,
                    }}
                >
                    <CartesianGrid strokeDasharray="1 9" />
                    <XAxis dataKey="name" tick={renderCustomAxisTick} />
                    <YAxis
                        width={80}
                        domain={[minY, maxY]}
                        tick={renderCustomYAxisTick}
                    />
                    <Tooltip
                        content={<CustomTooltip
                            fieldConfig={fieldConfig}
                            currencyName={currencyName}
                            currencyRate={currencyRate}
                        />}
                    />
                    {fieldConfig.map(config => (
                            <Line
                                key={config.field.toString()}
                                dot={false}
                                type="natural"
                                dataKey={config.field.toString()}
                                name={config.label || config.field.toString()}
                                stroke={config.color}
                                fill={config.color}
                                strokeWidth={config.strokeWidth || 1}
                            />
                    ))}
                    {referenceLines}
                    <Legend />
                </LineChart>
            </ResponsiveContainer>
        </Box>
    );
}

export default TeamRatingsChart;
