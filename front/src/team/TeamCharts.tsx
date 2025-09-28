import React from 'react';
import { useState, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import TeamLevelDataProps from './TeamLevelDataProps';
import ExecutableComponent, { type StateAndRequest } from '../common/sections/HookExecutableComponent';
import { getTeamRankingsRange } from '../rest/clients/TeamStatsClient';
import type TeamRankingsStats from '../rest/models/team/TeamRankingsStats';
import type TeamRanking from '../rest/models/team/TeamRanking';
import { Box, Card, CardContent, CardHeader, Slider, Tab, Tabs, Typography } from '@mui/material';
import '../common/Formatters.css';
import TeamRatingsChart, { type TeamRatingsChartProps } from './TeamRankingsChart';

interface TabPanelProps {
    children?: React.ReactNode;
    index: number;
    value: number;
}

function TabPanel(props: TabPanelProps) {
    const { children, value, index, ...other } = props;

    return (
        <Box
            role="tabpanel"
            hidden={value !== index}
            id={`team-stats-tabpanel-${index}`}
            aria-labelledby={`team-stats-tab-${index}`}
            {...other}
        >
            {value === index && (
                <Box sx={{ p: 3 }}>{children}</Box>
            )}
        </Box>
    );
}

function a11yProps(index: number) {
    return {
        id: `team-stats-tab-${index}`,
        'aria-controls': `team-stats-tabpanel-${index}`,
    };
}

interface Props {
    props: TeamLevelDataProps
}

interface Request {
    fromSeason: number;
    toSeason: number;
}

const TeamsCharts: React.FC<Props> = ({ props }) => {
    const { t } = useTranslation();
    const seasons = props.seasons();
    const minSeason = Math.min(...seasons);
    const maxSeason = Math.max(...seasons);

    // Configs using t
    const hatstatsConfig = [
        { field: 'hatstats' as keyof TeamRanking, label: t('table.hatstats'), color: '#0a720a', strokeWidth: 2 },
        { field: 'defense' as keyof TeamRanking, label: t('table.defense'), color: '#1976d2', strokeWidth: 1 },
        { field: 'attack' as keyof TeamRanking, label: t('table.attack'), color: '#d32f2f', strokeWidth: 1 },
        { field: 'midfield' as keyof TeamRanking, label: t('table.midfield'), color: '#fbc02d', strokeWidth: 1 }
    ];

    const ratingFieldConfig: TeamRatingsChartProps["fieldConfig"] = [
        {
            field: 'rating' as keyof TeamRanking,
            label: t('table.rating'),
            color: '#1976d2',
            strokeWidth: 2,
            format: {
                type: 'ratio',
                divideBy: 10,
                decimals: 1
            }
        },
        {
            field: 'ratingEndOfMatch' as keyof TeamRanking,
            label: t('table.rating_end_of_match'),
            color: '#0a720a',
            strokeWidth: 2,
            format: {
                type: 'ratio',
                divideBy: 10,
                decimals: 1
            }
        },
    ];

    const powerRatingConfig: TeamRatingsChartProps["fieldConfig"] = [
        {
            field: 'powerRating' as keyof TeamRanking,
            label: t('table.power_rating'),
            color: '#0a720a',
            strokeWidth: 2,
            format: {
                type: 'number',
                decimals: 0
            }
        },
    ];

    const tsiConfig: TeamRatingsChartProps["fieldConfig"] = [
        {
            field: 'tsi' as keyof TeamRanking,
            label: t('table.tsi_full'),
            color: '#0a720a',
            strokeWidth: 2,
            format: {
                type: 'number',
                decimals: 0
            }
        }
    ];

    const salaryConfig: TeamRatingsChartProps["fieldConfig"] = [
        {
            field: 'salary' as keyof TeamRanking,
            label: t('table.salary'),
            color: '#0a720a',
            strokeWidth: 2,
            format: {
                type: 'currency',
                decimals: 0,
                showCurrency: true
            }
        },
    ];

    const [seasonRange, setSeasonRange] = useState<[number, number]>([Math.max(minSeason, maxSeason - 1), maxSeason]);
    const [activeTab, setActiveTab] = useState(0);
    // Add a ref to track the widest range we've fetched
    const loadedRangeRef = useRef<[number, number]>([Math.max(minSeason, maxSeason - 1), maxSeason]);

    const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
        setActiveTab(newValue);
    };

    const initialRequest: Request = {
        fromSeason: seasonRange[0],
        toSeason: seasonRange[1],
    };

    return <ExecutableComponent<Request, TeamRankingsStats>
        initialRequest={initialRequest}
        responseToState={r => r!}
        executeRequest={(request, callback) => { getTeamRankingsRange(props.teamId(), request.fromSeason, request.toSeason, callback) }}
        content={(stateAndRequest: StateAndRequest<Request, TeamRankingsStats>) => {
            if (!stateAndRequest.currentState) {
                return <></>;
            }

            // Update loaded range ref with current data range
            const currentRequest = stateAndRequest.currentRequest;
            if (currentRequest) {
                loadedRangeRef.current = [
                    Math.min(loadedRangeRef.current[0], currentRequest.fromSeason),
                    Math.max(loadedRangeRef.current[1], currentRequest.toSeason)
                ];
            }

            const teamRankings = stateAndRequest.currentState.teamRankings
                .filter(tr => tr.rankType === 'league_id')
                .filter(tr => tr.season >= seasonRange[0] && tr.season <= seasonRange[1])
                .sort((a, b) => {
                    if (a.season !== b.season) {
                        return a.season - b.season;
                    }
                    return a.round - b.round;
                });

            const handleSeasonChange = (_: any, newValue: number | number[]) => {
                setSeasonRange(newValue as [number, number]);
            };

            const handleSeasonChangeCommitted = (_: any, newValue: number | number[]) => {
                const newRange = newValue as [number, number];
                setSeasonRange(newRange);

                // Check if the new range is already covered by our loaded data
                const [newFrom, newTo] = newRange;
                const [loadedFrom, loadedTo] = loadedRangeRef.current;

                // Only fetch if we need data outside our already loaded range
                if (newFrom < loadedFrom || newTo > loadedTo) {
                    // Update request with potentially wider range to avoid frequent refetches
                    const requestFrom = Math.min(newFrom, loadedFrom);
                    const requestTo = Math.max(newTo, loadedTo);
                    stateAndRequest.setRequest({
                        fromSeason: requestFrom,
                        toSeason: requestTo
                    });

                    // Update our loaded range reference immediately
                    loadedRangeRef.current = [requestFrom, requestTo];
                }
                // If range is within already loaded data, don't make a new request
            };

            const card = (
                <Card sx={{ mt: 2, boxShadow: '0 4px 8px rgba(0,0,0,0.1)' }}>
                    <CardHeader
                        title={t('menu.team_overview')}
                        sx={{
                            backgroundColor: '#f8f9fa', // Bootstrap light header
                            fontWeight: 500,
                            fontSize: '1.25rem',
                            borderBottom: '1px solid #dee2e6'
                        }}
                    />
                    <CardContent>
                        <Box sx={{ mb: 3, display: 'flex', flexDirection: 'column', alignItems: 'flex-start', width: '100%' }}>
                            <Typography variant="caption" color="textSecondary" sx={{ mb: 0.5 }}>
                                {t('team_charts.season_range')} {seasonRange[0]} - {seasonRange[1]}
                            </Typography>
                            <Slider
                                value={seasonRange}
                                onChange={handleSeasonChange}
                                onChangeCommitted={handleSeasonChangeCommitted}
                                valueLabelDisplay="auto"
                                min={minSeason}
                                max={maxSeason}
                                sx={{
                                    color: '#0a720a',
                                    width: '20%'
                                }}
                            />
                        </Box>
                        <Tabs
                            value={activeTab}
                            onChange={handleTabChange}
                            aria-label="team stats tabs"
                            sx={{ borderBottom: 1, borderColor: 'divider', mb: 2 }}
                        >
                            <Tab label={t('team_charts.tab.hatstats')} {...a11yProps(0)} />
                            <Tab label={t('team_charts.tab.rating')} {...a11yProps(1)} />
                            <Tab label={t('team_charts.tab.power_rating')} {...a11yProps(2)} />
                            <Tab label={t('team_charts.tab.salary')} {...a11yProps(3)} />
                            <Tab label={t('team_charts.tab.tsi')} {...a11yProps(4)} />
                        </Tabs>

                        <TabPanel value={activeTab} index={0}>
                            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
                                <Box sx={{ width: '100%', maxWidth: '1200px' }}>
                                    <TeamRatingsChart
                                        chartData={teamRankings}
                                        title={t('team_charts.hatstats.title')}
                                        fieldConfig={hatstatsConfig}
                                        emptyMessage={t('team_charts.hatstats.empty')}
                                        currencyName={stateAndRequest.currentState.currencyName}
                                        currencyRate={stateAndRequest.currentState.currencyRate}
                                    />
                                </Box>
                            </Box>
                        </TabPanel>

                        <TabPanel value={activeTab} index={1}>
                            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
                                <Box sx={{ width: '100%', maxWidth: '1200px' }}>
                                    <TeamRatingsChart
                                        chartData={teamRankings}
                                        title={t('team_charts.rating.title')}
                                        fieldConfig={ratingFieldConfig}
                                        emptyMessage={t('team_charts.rating.empty')}
                                        currencyName={stateAndRequest.currentState.currencyName}
                                        currencyRate={stateAndRequest.currentState.currencyRate}
                                    />
                                </Box>
                            </Box>
                        </TabPanel>

                        <TabPanel value={activeTab} index={2}>
                            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
                                <Box sx={{ width: '100%', maxWidth: '1200px' }}>
                                    <TeamRatingsChart
                                        chartData={teamRankings}
                                        title={t('team_charts.power_rating.title')}
                                        fieldConfig={powerRatingConfig}
                                        emptyMessage={t('team_charts.power_rating.empty')}
                                        currencyName={stateAndRequest.currentState.currencyName}
                                        currencyRate={stateAndRequest.currentState.currencyRate}
                                    />
                                </Box>
                            </Box>
                        </TabPanel>

                        <TabPanel value={activeTab} index={3}>
                            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
                                <Box sx={{ width: '100%', maxWidth: '1200px' }}>
                                    <TeamRatingsChart
                                        chartData={teamRankings}
                                        title={t('team_charts.salary.title')}
                                        fieldConfig={salaryConfig}
                                        emptyMessage={t('team_charts.salary.empty')}
                                        currencyName={stateAndRequest.currentState.currencyName}
                                        currencyRate={stateAndRequest.currentState.currencyRate}
                                    />
                                </Box>
                            </Box>
                        </TabPanel>

                        <TabPanel value={activeTab} index={4}>
                            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
                                <Box sx={{ width: '100%', maxWidth: '1200px' }}>
                                    <TeamRatingsChart
                                        chartData={teamRankings}
                                        title={t('team_charts.tsi.title')}
                                        fieldConfig={tsiConfig}
                                        emptyMessage={t('team_charts.tsi.empty')}
                                        currencyName={stateAndRequest.currentState.currencyName}
                                        currencyRate={stateAndRequest.currentState.currencyRate}
                                    />
                                </Box>
                            </Box>
                        </TabPanel>
                    </CardContent>
                </Card>
            );
            return card;
        }}
    />
}

export default TeamsCharts;
