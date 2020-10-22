import React from 'react';
import TeamRating from '../../rest/models/TeamRating';
import ModelTable, { ModelTablePropsWrapper, SortingState, ModelTableProps } from '../../common/ModelTable';
import ModelTableTh from '../../common/ModelTableTh'
import '../../i18n'
import { Translation } from 'react-i18next'
import { StatsTypeEnum } from '../../rest/StatisticsParameters';
import LevelData from '../../rest/models/LevelData';
import LeagueUnitLink from '../links/LeagueUnitLink';
import TeamLink from '../links/TeamLink'

abstract class TeamHatstats<Data extends LevelData, TableProps extends ModelTableProps<Data>> 
    extends ModelTable<Data, TableProps, TeamRating> {
    constructor(props: ModelTablePropsWrapper<Data, TableProps>) {
        super(props, 'hatstats', {statType: StatsTypeEnum.AVG},
            [StatsTypeEnum.AVG, StatsTypeEnum.MAX, StatsTypeEnum.ROUND])
    }
    
    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th>{t('table.team')}</th>
                <th className="value">{t('table.league')}</th>
                <ModelTableTh title='table.hatstats' sortingField='hatstats' sortingState={sortingState} />
                <ModelTableTh title='table.midfield' sortingField='midfield' sortingState={sortingState} />
                <ModelTableTh title='table.defense' sortingField='defense' sortingState={sortingState} />
                <ModelTableTh title='table.attack' sortingField='attack' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, teamRating: TeamRating): JSX.Element {
        return <tr key={"team_hatstats_row_" + index}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamRating.teamId} name={teamRating.teamName} /></td>
            <td className="value"><LeagueUnitLink id={teamRating.leagueUnitId} name={teamRating.leagueUnitName}/></td>
            <td className="value">{teamRating.hatStats}</td>
            <td className="value">{teamRating.midfield * 3}</td>
            <td className="value">{teamRating.defense}</td>
            <td className="value">{teamRating.attack}</td>
        </tr>
    }

}

export default TeamHatstats