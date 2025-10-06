import { Select, MenuItem, FormControl, InputLabel, SelectChangeEvent, Box } from '@mui/material';
import ExecutableComponent from '../../../common/sections/HookExecutableComponent';
import { TeamHatstatsChart } from '../../../rest/models/team/TeamHatstats';
import LeagueUnitLevelDataProps from '../../LeagueUnitLevelDataProps';
import { teamHatstatsChart } from '../../../rest/clients/LeagueUnitClient';
import { useTranslation } from 'react-i18next';
import { LevelDataPropsWrapper } from '../../../common/LevelDataProps';
import { useState } from 'react';
import TeamHatstatsCharts from './TeamHatstatsCharts';
import TeamHatstatsTable from './TeamHatstatsTable';

interface ChartEntry {
    season: number,
    round: number,
    teams: Record<string, TeamHatstatsChart>
}

const TeamHatstatsChartSectionContent = (props: {
    levelDataProps: LeagueUnitLevelDataProps,
    stateAndRequest: {
        currentState: TeamHatstatsChart[],
        currentRequest: number,
        setRequest: (request: number) => void
    }
}) => {
    const { levelDataProps, stateAndRequest } = props;
    const [round, setRound] = useState<number | 'avg' | 'max'>('max');
    const { t } = useTranslation();

    if (stateAndRequest.currentState.length === 0) {
        return <div>{t('team.no_data')}</div>
    }

    const chartData = stateAndRequest.currentState.map(ch => {
        return {
            ...ch, season: stateAndRequest.currentRequest
        }
    })
    const map = new Map<number, Record<string, TeamHatstatsChart>>()

    for (const cd of chartData) {
        if (map.has(cd.round)) {
            const existing = map.get(cd.round)
            existing![cd.teamSortingKey.teamName] = cd
        } else {
            map.set(cd.round, { [cd.teamSortingKey.teamName]: cd })
        }
    }

    const names = new Set(chartData.map(cd => cd.teamSortingKey.teamName))

    const chartEntries: ChartEntry[] = []
    map.forEach((value, key) => {
        chartEntries.push({ season: stateAndRequest.currentRequest, round: key, teams: value })
    })

    const chartColors = [
        '#0a720a', // green
        '#1e90ff', // blue
        '#ff6347', // tomato
        '#ffd700', // gold
        '#8a2be2', // blue violet
        '#ff69b4', // hot pink
        '#20b2aa', // light sea green
        '#ff8c00'  // dark orange
    ];
    const maxRound = chartEntries.reduce((max, entry) => Math.max(max, entry.round), 0);

    const lastEntry = map.get(maxRound)!

    const configsFactory = (fieldFunction: (thc: TeamHatstatsChart) => number | undefined) => {
        const sortedTeamNames = Object.keys(lastEntry).sort((a, b) => {
            return (lastEntry[b].hatStats) - (lastEntry[a].hatStats);
        });

        const allTeamNames = [...sortedTeamNames];
        names.forEach(name => {
            if (!allTeamNames.includes(name)) {
                allTeamNames.push(name);
            }
        });

        return allTeamNames.map((teamName, index) => {
            return {
                fieldFunction: (entry: ChartEntry) => {
                    return (entry.teams[teamName]) ? fieldFunction(entry.teams[teamName]) : undefined
                },
                label: teamName,
                color: chartColors[index % chartColors.length],
                strokeWidth: 1
            }
        })
    }

    const availableSeasons = levelDataProps.seasons();
    const roundsForSeason = levelDataProps.rounds(stateAndRequest.currentRequest);

    const handleSeasonChange = (event: SelectChangeEvent<number>) => {
        const newSeason = event.target.value as number;
        let newRound;
        if (newSeason !== stateAndRequest.currentRequest) {
            const rounds = levelDataProps.rounds(newSeason)
            newRound = rounds[rounds.length - 1];
        } else {
            newRound = round;
        }

        stateAndRequest.setRequest(newSeason);
        setRound(newRound);
    };

    const handleRoundChange = (event: SelectChangeEvent<number | 'avg' | 'max'>) => {
        setRound(event.target.value as number)
    };

    const dataToShow: Record<string, TeamHatstatsChart> = (() => {
        if (round === 'avg' || round === 'max') {
            const teamData: Record<string, TeamHatstatsChart[]> = {};
            for (const cd of chartData) {
                const teamName = cd.teamSortingKey.teamName;
                if (!teamData[teamName]) {
                    teamData[teamName] = [];
                }
                teamData[teamName].push(cd);
            }

            const aggregatedData: Record<string, TeamHatstatsChart> = {};
            for (const teamName in teamData) {
                const teamEntries = teamData[teamName];
                if (round === 'avg') {
                    const count = teamEntries.length;

                    const sum = (getter: (entry: TeamHatstatsChart) => number) => teamEntries.reduce((acc, curr) => acc + getter(curr), 0);

                    aggregatedData[teamName] = {
                        ...teamEntries[0],
                        round: 0,
                        hatStats: Math.round(sum(e => e.hatStats) / count),
                        midfield: Math.round(sum(e => e.midfield) / count),
                        defense: Math.round(sum(e => e.defense) / count),
                        attack: Math.round(sum(e => e.attack) / count),
                        loddarStats: Math.round(sum(e => e.loddarStats) / count),
                    };
                } else { // round === 'max'
                    const max = (getter: (entry: TeamHatstatsChart) => number) => Math.max(...teamEntries.map(getter));

                    const maxHatStatsEntry = teamEntries.reduce((prev, current) => (prev.hatStats > current.hatStats) ? prev : current);

                    aggregatedData[teamName] = {
                        ...maxHatStatsEntry,
                        hatStats: max(e => e.hatStats),
                        midfield: max(e => e.midfield),
                        defense: max(e => e.defense),
                        attack: max(e => e.attack),
                        loddarStats: max(e => e.loddarStats),
                    };
                }
            }
            return aggregatedData;
        } else {
            return map.get(round) ?? {};
        }
    })();
    return <Box sx={{ m: 2 }}>
        <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel id="season-select-label">{t('filter.season')}</InputLabel>
            <Select
                labelId="season-select-label"
                value={stateAndRequest.currentRequest}
                label={t('filter.season')}
                onChange={handleSeasonChange}
            >
                {availableSeasons.map(season => (
                    <MenuItem key={season} value={season}>
                        {season + props.levelDataProps.seasonOffset()}
                    </MenuItem>
                ))}
            </Select>
        </FormControl>

        <FormControl size="small" sx={{ minWidth: 120, ml: 2 }}>
            <InputLabel id="round-select-label">{t('filter.round')}</InputLabel>
            <Select
                labelId="round-select-label"
                value={round}
                label={t('filter.round')}
                onChange={handleRoundChange}
            >
                {roundsForSeason.map(round => (
                    <MenuItem key={round} value={round}>
                        {round}
                    </MenuItem>
                ))}
                <MenuItem key='avg' value='avg'>
                    avg
                </MenuItem>
                <MenuItem key='max' value='max'>
                    max
                </MenuItem>
            </Select>
        </FormControl>

        <TeamHatstatsCharts chartEntries={chartEntries} configsFactory={configsFactory} leagueUnitLevelProps={levelDataProps} />

        <TeamHatstatsTable data={Object.values(dataToShow)} />
    </Box>
}


const TeamHatstatsChartSection = (props: LevelDataPropsWrapper<LeagueUnitLevelDataProps>) => {
    const leagueUnitLevelProps = props.levelDataProps;

    return <ExecutableComponent<number, TeamHatstatsChart[]>
        initialRequest={leagueUnitLevelProps.currentSeason()}
        executeRequest={(request, callback) => teamHatstatsChart(leagueUnitLevelProps.leagueUnitId(), request, callback)}
        responseToState={(response => response ?? [])}
        content={stateAndRequest => <TeamHatstatsChartSectionContent levelDataProps={leagueUnitLevelProps} stateAndRequest={stateAndRequest} />}
    />
}

export default TeamHatstatsChartSection;
