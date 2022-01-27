import React from 'react'
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import LevelData from '../../../rest/models/leveldata/LevelData';
import TeamStreakTrophies from '../../../rest/models/team/TeamStreakTrophies';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import { getTeamStreakTrophies } from '../../../rest/Client';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../elements/SortingTableTh'
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import HattidTooltip from '../../elements/HattidTooltip';

abstract class TeamStreakTrophiesTable<Data extends LevelData, TableProps extends LevelDataProps<Data>>
    extends ClassicTableSection<Data, TableProps, TeamStreakTrophies> {

    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'trophies_number', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getTeamStreakTrophies

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <HattidTooltip 
                    poppedHint={t('table.position')}
                    content={<th>{t('table.position_abbr')}</th>}
                />
                <th>{t('table.team')}</th>
                <th className="text-center">{t('table.league')}</th>
                <ModelTableTh title='table.trophies' sortingField='trophies_number' sortingState={sortingState} />
                <ModelTableTh title='table.victories' sortingField='number_of_victories' sortingState={sortingState} />
                <ModelTableTh title='table.undefeated' sortingField='number_of_undefeated' sortingState={sortingState} />
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
