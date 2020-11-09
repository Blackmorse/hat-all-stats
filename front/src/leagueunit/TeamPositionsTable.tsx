import React from 'react';
import LeagueUnitData from '../rest/models/leveldata/LeagueUnitData'
import ModelTable, { SortingState } from '../common/sections/TableSection'
import { LevelDataPropsWrapper } from '../common/LevelDataProps'
import { StatsTypeEnum } from '../rest/models/StatisticsParameters';
import TeamPosition from '../rest/models/team/TeamPosition';
import LeagueUnitLevelDataProps from './LeagueUnitLevelDataProps';
import { getTeamPositions } from '../rest/Client'
import '../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../common/elements/SortingTableTh';
import TeamLink from '../common/links/TeamLink'

class TeamPositionsTable extends ModelTable<LeagueUnitData, LeagueUnitLevelDataProps, TeamPosition> {
    
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
                    <th>{t('table.team')}</th>
                    <th className="value hint" popped-hint={t('table.games')}>{t('table.games_abbr')}</th>
                    <ModelTableTh poppedHint={t('table.win')} title='table.win_abbr' sortingField='win' sortingState={sortingState}/>
                    <ModelTableTh poppedHint={t('table.draw')} title='table.draw_abbr' sortingField='draw' sortingState={sortingState} />
                    <ModelTableTh poppedHint={t('table.lose')} title='table.lose_abbr' sortingField='lost' sortingState={sortingState} />
                    <ModelTableTh poppedHint={t('table.goals_for')} title='table.goals_for_abbr' sortingField='scored' sortingState={sortingState} />
                    <ModelTableTh poppedHint={t('table.goals_against')} title='table.goals_against_abbr' sortingField='missed' sortingState={sortingState} />
                
                    <ModelTableTh title='table.points' sortingField='points' sortingState={sortingState} />
                </tr>
            }
        </Translation>
    }


    columnValues(index: number, teamPosition: TeamPosition): JSX.Element {
        return <tr key={"team_positions_row" + index}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamPosition.teamId} text={teamPosition.teamName} /></td>
            <td className="value">{teamPosition.games}</td>
            <td className="value">{teamPosition.win}</td>
            <td className="value">{teamPosition.draw}</td>
            <td className="value">{teamPosition.lost}</td>
            <td className="value">{teamPosition.scored}</td>
            <td className="value">{teamPosition.missed}</td>
            <td className="value">{teamPosition.points}</td>
        </tr>
    }

}

export default TeamPositionsTable