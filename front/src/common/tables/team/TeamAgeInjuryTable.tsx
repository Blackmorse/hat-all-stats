import React from 'react';
import TeamAgeInjury from "../../../rest/models/team/TeamAgeInjury";
import LevelData from '../../../rest/models/leveldata/LevelData';
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import '../../../i18n'
import { Translation } from 'react-i18next'
import { StatsTypeEnum } from "../../../rest/models/StatisticsParameters";
import { getTeamAgeInjuries } from '../../../rest/Client';
import ModelTableTh from '../../../common/elements/SortingTableTh'
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import { injuryFormatter, ageFormatter } from '../../Formatters'

abstract class TeamAgeInjuryTable<Data extends LevelData, TableProps extends LevelDataProps<Data>>
    extends ClassicTableSection<Data, TableProps, TeamAgeInjury> {

    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'age', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getTeamAgeInjuries

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th>{t('table.team')}</th>
                <th className="value">{t('table.league')}</th>
                <ModelTableTh title='table.age' sortingField='age' sortingState={sortingState} />
                <ModelTableTh title='table.total_injury_weeks' sortingField='injury' sortingState={sortingState} />
                <ModelTableTh title='table.total_injury_number' sortingField='injury_count' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, teamAgeInjury: TeamAgeInjury): JSX.Element {
        let teamSortingKey = teamAgeInjury.teamSortingKey
        return <>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} text={teamSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={teamSortingKey.leagueUnitId} text={teamSortingKey.leagueUnitName}/></td>
            <td className="value">{ageFormatter(teamAgeInjury.age)}</td>
            <td className="value">{injuryFormatter(teamAgeInjury.injury)}</td>
            <td className="value">{teamAgeInjury.injuryCount}</td>
        </>
    }
}

export default TeamAgeInjuryTable