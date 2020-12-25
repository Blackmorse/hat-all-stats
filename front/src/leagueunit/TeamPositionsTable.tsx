import React from 'react';
import LeagueUnitData from '../rest/models/leveldata/LeagueUnitData'
import { SortingState } from '../common/tables/AbstractTableSection'
import ClassicTableSection from '../common/tables/ClassicTableSection'
import { LevelDataPropsWrapper } from '../common/LevelDataProps'
import { StatsTypeEnum } from '../rest/models/StatisticsParameters';
import TeamPositionWithDiff from '../rest/models/team/TeamPosition';
import LeagueUnitLevelDataProps from './LeagueUnitLevelDataProps';
import { getTeamPositions } from '../rest/Client'
import '../i18n'
import { Translation } from 'react-i18next'
import SortingTableTh from '../common/elements/SortingTableTh';
import TeamLink from '../common/links/TeamLink'
import '../common/elements/Trends.css'

class TeamPositionsTable extends ClassicTableSection<LeagueUnitData, LeagueUnitLevelDataProps, TeamPositionWithDiff> {
    
    constructor(props: LevelDataPropsWrapper<LeagueUnitData, LeagueUnitLevelDataProps>) {
        super(props, 'points', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getTeamPositions
    
    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {(t, { i18n }) => 
                <tr>
                    <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                    <th></th>
                    <th>{t('table.team')}</th>
                    <th className="value hint" popped-hint={t('table.games')}>{t('table.games_abbr')}</th>
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


    columnValues(index: number, teamPositionWithDiff: TeamPositionWithDiff): JSX.Element {
        let teamPosition = teamPositionWithDiff.leagueUnitTeamStat
        let trend: JSX.Element = <img src="/trend-gray.png" alt="same" />
        if(teamPositionWithDiff.positionDiff < 0) {
            trend = <img className="trend_up" src="/trend-green.png" alt="up" />
        } else if (teamPositionWithDiff.positionDiff > 0) {
            trend = <img className="trend_down" src="/trend-red.png" alt="down" />
        }
        return <>
            <td>{index + 1}</td>
            <td>{trend}</td>
            <td><TeamLink id={teamPosition.teamId} text={teamPosition.teamName} /></td>
            <td className="value">{teamPosition.games}</td>
            <td className="value">{teamPosition.win}</td>
            <td className="value">{teamPosition.draw}</td>
            <td className="value">{teamPosition.lost}</td>
            <td className="value">{teamPosition.scored}</td>
            <td className="value">{teamPosition.missed}</td>
            <td className="value">{teamPosition.points}</td>
        </>
    }

}

export default TeamPositionsTable