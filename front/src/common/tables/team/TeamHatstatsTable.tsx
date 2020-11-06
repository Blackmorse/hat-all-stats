import React from 'react';
import TeamHatstats from '../../../rest/models/team/TeamHatstats';
import ModelTable, { ModelTablePropsWrapper, SortingState, ModelTableProps } from '../../ModelTable';
import ModelTableTh from '../../ModelTableTh'
import '../../../i18n'
import { Translation } from 'react-i18next'
import { StatsTypeEnum } from '../../../rest/StatisticsParameters';
import LevelData from '../../../rest/models/leveldata/LevelData';
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import { getTeamHatstats } from '../../../rest/Client';

abstract class TeamHatstatsTable<Data extends LevelData, TableProps extends ModelTableProps<Data>> 
    extends ModelTable<Data, TableProps, TeamHatstats> {
    constructor(props: ModelTablePropsWrapper<Data, TableProps>) {
        super(props, 'hatstats', {statType: StatsTypeEnum.AVG},
            [StatsTypeEnum.AVG, StatsTypeEnum.MAX, StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getTeamHatstats
    
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

    columnValues(index: number, teamHatstats: TeamHatstats): JSX.Element {
        return <tr key={"team_hatstats_row_" + index}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamHatstats.teamId} name={teamHatstats.teamName} /></td>
            <td className="value"><LeagueUnitLink id={teamHatstats.leagueUnitId} name={teamHatstats.leagueUnitName}/></td>
            <td className="value">{teamHatstats.hatStats}</td>
            <td className="value">{teamHatstats.midfield * 3}</td>
            <td className="value">{teamHatstats.defense}</td>
            <td className="value">{teamHatstats.attack}</td>
        </tr>
    }

}

export default TeamHatstatsTable