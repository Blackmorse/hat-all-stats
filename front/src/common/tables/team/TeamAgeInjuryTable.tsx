import React from 'react';
import TeamAgeInjury from "../../../rest/models/team/TeamAgeInjury";
import LevelData from '../../../rest/models/leveldata/LevelData';
import ModelTable, { ModelTablePropsWrapper, SortingState, ModelTableProps } from '../../../common/ModelTable';
import '../../../i18n'
import { Translation } from 'react-i18next'
import { StatsTypeEnum } from "../../../rest/models/StatisticsParameters";
import { getTeamAgeInjuries } from '../../../rest/Client';
import ModelTableTh from '../../../common/elements/ModelTableTh'
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import { injuryFormatter, ageFormatter } from '../../Formatters'

abstract class TeamAgeInjuryTable<Data extends LevelData, TableProps extends ModelTableProps<Data>>
    extends ModelTable<Data, TableProps, TeamAgeInjury> {

    constructor(props: ModelTablePropsWrapper<Data, TableProps>) {
        super(props, 'age', {statType: StatsTypeEnum.ROUND, roundNumber: props.modelTableProps.currentRound()},
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
        return <tr key={"team_salary_tsi_row_" + index}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} name={teamSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={teamSortingKey.leagueUnitId} name={teamSortingKey.leagueUnitName}/></td>
            <td className="value">{ageFormatter(teamAgeInjury.age)}</td>
            <td className="value">{injuryFormatter(teamAgeInjury.injury)}</td>
            <td className="value">{teamAgeInjury.injuryCount}</td>
        </tr>
    }
}

export default TeamAgeInjuryTable