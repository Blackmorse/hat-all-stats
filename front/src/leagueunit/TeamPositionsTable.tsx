import React from 'react';
import LeagueUnitData from '../rest/models/leveldata/LeagueUnitData'
import { DataRequest, SortingState } from '../common/tables/AbstractTableSection'
import AbstractTableSection from '../common/tables/AbstractTableSection'
import { LevelDataPropsWrapper } from '../common/LevelDataProps'
import { StatsTypeEnum } from '../rest/models/StatisticsParameters';
import LeagueUnitTeamStatsWithPositionDiff, { LeagueUnitTeamStatHistoryInfo } from '../rest/models/team/LeagueUnitTeamStat';
import LeagueUnitLevelDataProps from './LeagueUnitLevelDataProps';
import { getTeamPositions } from '../rest/Client'
import '../i18n'
import { Translation } from 'react-i18next'
import SortingTableTh from '../common/elements/SortingTableTh';
import TeamLink from '../common/links/TeamLink'
import '../common/elements/Trends.css'
import { SelectorsEnum } from '../common/tables/SelectorsEnum';
import { LoadingEnum } from '../common/enums/LoadingEnum';
import RestTableData from '../rest/models/RestTableData';
import TeamPositionsChart from './TeamPositionsChart'
import Section from '../common/sections/Section';

class TeamPositionsTableBase extends AbstractTableSection<LeagueUnitData, LeagueUnitLevelDataProps, LeagueUnitTeamStatsWithPositionDiff, LeagueUnitTeamStatHistoryInfo> {
        
    constructor(props: LevelDataPropsWrapper<LeagueUnitData, LeagueUnitLevelDataProps>) {
        super(props, 'points', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND],
            [SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR])
    }

    responseModelToRowModel(responseModel?: LeagueUnitTeamStatHistoryInfo): RestTableData<LeagueUnitTeamStatsWithPositionDiff> {
        return {
            entities: (responseModel === undefined) ? [] : responseModel.teamsLastRoundWithPositionsDiff,
            isLastPage: true
        }
    }
    executeDataRequest(dataRequest: DataRequest, callback: (loadingState: LoadingEnum, result?: LeagueUnitTeamStatHistoryInfo) => void): void {
        const leveRequest = this.props.levelDataProps.createLevelRequest()
        getTeamPositions(leveRequest, dataRequest.statisticsParameters, callback)
    }

    fetchDataFunction = getTeamPositions
    
    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {(t, { i18n }) => 
                <tr>
                    <th className="hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                    <th></th>
                    <th>{t('table.team')}</th>
                    <th className="text-center hint" popped-hint={t('table.games')}>{t('table.games_abbr')}</th>
                    <SortingTableTh poppedHint={t('table.win')} title='table.win_abbr' sortingField='win' sortingState={sortingState}/>
                    <SortingTableTh poppedHint={t('table.draw')} title='table.draw_abbr' sortingField='draw' sortingState={sortingState} />
                    <SortingTableTh poppedHint={t('table.lose')} title='table.lose_abbr' sortingField='lost' sortingState={sortingState} />
                    <SortingTableTh poppedHint={t('table.goals_for')} title='table.goals_for_abbr' sortingField='scored' sortingState={sortingState} />
                    <SortingTableTh poppedHint={t('table.goals_against')} title='table.goals_against_abbr' sortingField='missed' sortingState={sortingState} />
                
                    <SortingTableTh title='table.points' sortingField='points' sortingState={sortingState} />
                </tr>
            }
        </Translation>
    }


    row(index: number, className: string, teamPositionWithDiff: LeagueUnitTeamStatsWithPositionDiff): JSX.Element {
        let teamPosition = teamPositionWithDiff.leagueUnitTeamStat
        let trend: JSX.Element = <img src="/trend-gray.png" alt="same" />
        if(teamPositionWithDiff.positionDiff < 0) {
            trend = <img className="trend_up" src="/trend-green.png" alt="up" />
        } else if (teamPositionWithDiff.positionDiff > 0) {
            trend = <img className="trend_down" src="/trend-red.png" alt="down" />
        }
        return <tr className={className}>
            <td>{index + 1}</td>
            <td>{trend}</td>
            <td><TeamLink id={teamPosition.teamId} text={teamPosition.teamName} /></td>
            <td className="text-center">{teamPosition.games}</td>
            <td className="text-center">{teamPosition.win}</td>
            <td className="text-center">{teamPosition.draw}</td>
            <td className="text-center">{teamPosition.lost}</td>
            <td className="text-center">{teamPosition.scored}</td>
            <td className="text-center">{teamPosition.missed}</td>
            <td className="text-centers">{teamPosition.points}</td>
        </tr>
    }

    additionalSection(model?: LeagueUnitTeamStatHistoryInfo): JSX.Element {
        return <TeamPositionsChart leagueUnitTeamStatHistoryInfo={model} />
    }
}

const TeamPositionsTable = Section(TeamPositionsTableBase)
export default TeamPositionsTable