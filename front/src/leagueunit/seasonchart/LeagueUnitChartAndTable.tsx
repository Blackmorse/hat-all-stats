import { type JSX } from "react";
import { useState } from "react";
import ExecutableComponent, { StateAndRequest } from "../../common/sections/HookExecutableComponent"
import LeagueUnitLevelDataProps from "../LeagueUnitLevelDataProps"
import { LoadingEnum } from "../../common/enums/LoadingEnum";
import { useTranslation } from "react-i18next";
import TeamSortingKey from "../../rest/models/team/TeamSortingKey";
import { Box, FormControl, InputLabel, MenuItem, Select, SelectChangeEvent } from "@mui/material";
import LeagueUnitTeamsSeasonCharts from "./LeagueUnitTeamSeasonCharts";
import LeagueUnitTeamSeasonTable from "./LeagueUnitTeamSeasonTable";
import { ChartDataProps, ChartFormat, TimeSeries } from "../../common/charts/RechartsSeasonChart";
import { RequestParams } from "../../rest/clients/LeagueUnitClient";

export const genericAvgMerge = <T extends object>(data: T[], numericKeys: (keyof T)[]): T | undefined => {
    if (!data || data.length === 0) {
        return undefined;
    }

    const sums: { [K in keyof T]?: number } = {};
    numericKeys.forEach(key => sums[key] = 0);

    data.forEach(item => {
        numericKeys.forEach(key => {
            if (typeof item[key] === 'number') {
                (sums[key] as number) += item[key] as number;
            }
        });
    });

    const avgResult: T = { ...data[0] };
    numericKeys.forEach(key => {
        if (sums[key] !== undefined) {
            (avgResult as any)[key] = Math.round((sums[key] as number) / data.length);
        }
    });

    return avgResult;
};

export const genericMaxMerge = <T extends object>(data: T[], numericKeys: (keyof T)[]): T | undefined => {
    if (!data || data.length === 0) {
        return undefined;
    }

    const maxes: { [K in keyof T]?: number } = {};

    data.forEach(item => {
        numericKeys.forEach(key => {
            if (typeof item[key] === 'number') {
                if (maxes[key] === undefined) {
                    maxes[key] = item[key] as number;
                } else {
                    (maxes[key] as number) = Math.max((maxes[key] as number), item[key] as number);
                }
            }
        });
    });

    const maxResult: T = { ...data[0] };
    numericKeys.forEach(key => {
        if (maxes[key] !== undefined) {
            (maxResult as any)[key] = maxes[key];
        }
    });

    return maxResult;
};


interface ChartEntry<Data> {
    season: number,
    round: number,
    teams: Record<string, Data>
}

export interface ChartDefinition<Data extends { season: number, round: number }> {
    fieldFunction: (entry: ChartEntry<Data>) => number | undefined,
    label: string,
    color: string,
    strokeWidth: number,
    title: string,
    chartDataProps: ChartDataProps<Data>,
}

const LeagueUnitChartAndTableContent = <Data extends ChartData>(props: {
    levelDataProps: LeagueUnitLevelDataProps,
    stateAndRequest: StateAndRequest<RequestParams, Data[]>
    charts: ChartConfig<Data>[],
    avgMerger?: (entries: Data[]) => Data | undefined,
    maxMerger?: (entries: Data[]) => Data | undefined,
}) => {
    const { levelDataProps, stateAndRequest, avgMerger, maxMerger } = props;
    const [round, setRound] = useState<number | 'avg' | 'max'>(maxMerger ? 'max' : levelDataProps.currentRound());
    const { t } = useTranslation();

    if (stateAndRequest.currentState.length === 0) {
        return <div>{t('team.no_data')}</div>
    }

    const chartData = stateAndRequest.currentState.map(ch => {
        return {
            ...ch, season: stateAndRequest.currentRequest.season
        }
    })
    const map = new Map<number, Record<string, Data>>()

    for (const cd of chartData) {
        if (map.has(cd.round)) {
            const existing = map.get(cd.round)
            existing![cd.teamSortingKey.teamName] = cd
        } else {
            map.set(cd.round, { [cd.teamSortingKey.teamName]: cd })
        }
    }

    const chartEntries: ChartEntry<Data>[] = []
    map.forEach((value, key) => {
        chartEntries.push({ season: stateAndRequest.currentRequest.season, round: key, teams: value })
    })

    const availableSeasons = levelDataProps.seasons();
    const roundsForSeason = levelDataProps.rounds(stateAndRequest.currentRequest.season);

    const handleSeasonChange = (event: SelectChangeEvent<number>) => {
        const newSeason = event.target.value as number;
        let newRound;
        if (newSeason !== stateAndRequest.currentRequest.season) {
            const rounds = levelDataProps.rounds(newSeason)
            newRound = rounds[rounds.length - 1];
        } else {
            newRound = round;
        }

        stateAndRequest.setRequest({ ...stateAndRequest.currentRequest, season: newSeason });
        setRound(newRound);
    };

    const handleRoundChange = (event: SelectChangeEvent<number | 'avg' | 'max'>) => {
        setRound(event.target.value as number)
    };

    const dataToShow: Record<string, Data> = (() => {
        if (round === 'avg' || round === 'max') {
            const teamData: Record<string, Data[]> = {};
            for (const cd of chartData) {
                const teamName = cd.teamSortingKey.teamName;
                if (!teamData[teamName]) {
                    teamData[teamName] = [];
                }
                teamData[teamName].push(cd);
            }

            const aggregatedData: Record<string, Data> = {};
            for (const teamName in teamData) {
                const teamEntries = teamData[teamName];
                if (round === 'avg' && avgMerger) {
                    const merged = avgMerger(teamEntries);
                    if (merged) {
                        aggregatedData[teamName] = merged;
                    }
                } else if (maxMerger) { // round === 'max'
                    const merged = maxMerger(teamEntries);
                    if (merged) {
                        aggregatedData[teamName] = merged;
                    }
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
                value={stateAndRequest.currentRequest.season}
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
                {props.avgMerger && <MenuItem key='avg' value='avg'>
                    avg
                </MenuItem>}
                {props.maxMerger && <MenuItem key='max' value='max'>
                    max
                </MenuItem>}
            </Select>
        </FormControl>

        <LeagueUnitTeamsSeasonCharts<Data>
            chartEntries={chartEntries}
            leagueUnitLevelProps={levelDataProps}
            charts={props.charts} />
        <LeagueUnitTeamSeasonTable<Data>
            dataToShow={dataToShow}
            charts={props.charts}
        />
    </Box>
}

export type ChartData = TimeSeries & { teamSortingKey: TeamSortingKey };

export interface ChartConfig<Data extends ChartData> {
    field: (data: Data) => number | undefined,
    fieldFormatted?: (data: Data) => string | number | JSX.Element,
    label: string,
    format?: ChartFormat
}

const LeagueUnitChartAndTable = <Data extends { season: number, round: number; teamSortingKey: TeamSortingKey }>(props: {
    initialRequestParams: RequestParams,
    levelDataProps: LeagueUnitLevelDataProps,
    executeRequestCallback: (leagueUnitId: number, requestParams: RequestParams, callback: (loadingEnum: LoadingEnum, result?: Data[]) => void) => void,
    charts: ChartConfig<Data>[]
    avgMerger?: (entries: Data[]) => Data | undefined,
    maxMerger?: (entries: Data[]) => Data | undefined,
}) => {
    return <ExecutableComponent<RequestParams, Data[]>
        initialRequest={props.initialRequestParams}
        executeRequest={(request, callback) => props.executeRequestCallback(props.levelDataProps.leagueUnitId(), request, callback)}
        responseToState={(response => response ?? [])}
        content={stateAndRequest =>
            <LeagueUnitChartAndTableContent<Data>
                levelDataProps={props.levelDataProps}
                stateAndRequest={stateAndRequest}
                charts={props.charts}
                avgMerger={props.avgMerger}
                maxMerger={props.maxMerger}
            />
        }
    />

}

export default LeagueUnitChartAndTable;
