import React from 'react'
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TeamStreakTrophies from '../../../rest/models/team/TeamStreakTrophies';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import { getTeamStreakTrophies } from '../../../rest/Client';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../elements/SortingTableTh'
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import HattidTooltip from '../../elements/HattidTooltip';

abstract class TeamStreakTrophiesTable<TableProps extends LevelDataProps>
    extends ClassicTableSection<TableProps, TeamStreakTrophies> {

    constructor(props: LevelDataPropsWrapper<TableProps>) {
        super(props, 'trophies_number', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getTeamStreakTrophies

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            t =>
            <tr>
                <HattidTooltip 
                    poppedHint={t('table.position')}
                    content={<th>{t('table.position_abbr')}</th>}
                />
                <th>{t('table.team')}</th>
                <th className="text-center">{t('table.league')}</th>
                <ModelTableTh title='table.trophies' sorting={{field: 'trophies_number', state: sortingState}} />
                <ModelTableTh title='table.victories' sorting={{field: 'number_of_victories', state: sortingState}} />
                <ModelTableTh title='table.undefeated' sorting={{field: 'number_of_undefeated', state: sortingState}} />
            </tr>
        }
        </Translation>
    }

    row(index: number, className: string, teamStreakTrophies: TeamStreakTrophies): JSX.Element {
        let teamSortingKey = teamStreakTrophies.teamSortingKey
        return <tr className={className}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} text={teamSortingKey.teamName} /></td>
            <td className="text-center"><LeagueUnitLink id={teamSortingKey.leagueUnitId} text={teamSortingKey.leagueUnitName}/></td>
            <td className="text-center">{teamStreakTrophies.trophiesNumber}</td>
            <td className="text-center">{teamStreakTrophies.numberOfVictories}</td>
            <td className="text-center">{teamStreakTrophies.numberOfUndefeated}</td>
        </tr>
    }
}

export default TeamStreakTrophiesTable
