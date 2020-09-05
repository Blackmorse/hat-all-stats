import React from 'react';
import LeagueUnitRating from '../../rest/models/LeagueUnitRating'
import ModelTable, { ModelTablePropsWrapper, ModelTableProps } from '../ModelTable'
import ModelTableTh from '../ModelTableTh'
import { Translation } from 'react-i18next'
import '../../i18n'
import { StatsTypeEnum } from '../../rest/StatisticsParameters';
import LevelData from '../../rest/models/LevelData';

abstract class LeagueUnits<Data extends LevelData> extends ModelTable<Data, LeagueUnitRating> {

    constructor(props: ModelTablePropsWrapper<Data, ModelTableProps<Data>>) {
        super(props, 'menu.best_league_units', 
            'hatstats', {statType: StatsTypeEnum.AVG}, 
            [StatsTypeEnum.AVG, StatsTypeEnum.MAX, StatsTypeEnum.ROUND])
    }

    columnHeaders(): JSX.Element {
        const sortingState = {
            callback: this.sortingChanged,
            currentSorting: this.state.statisticsParameters.sortingField,
            sortingDirection: this.state.statisticsParameters.sortingDirection
        }

        return <Translation>{
            (t, { i18n }) => <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>              
                <th className="value">{t('table.league')}</th>
                <ModelTableTh title='table.hatstats' sortingField='hatstats'  sortingState={sortingState}/>
                <ModelTableTh title='table.midfield' sortingField='midfield' sortingState={sortingState}/>
                <ModelTableTh title='table.defense' sortingField='defense' sortingState={sortingState}/>
                <ModelTableTh title='table.attack' sortingField='attack' sortingState={sortingState}/>
            </tr>
            }
            </Translation> 
    }

    columnValues(index: number, leagueUnitRating: LeagueUnitRating): JSX.Element {
        return <tr key={"league_unit_row_" + index}>
            <td>{index + 1}</td>
            <td className="value"><a className="table_link" href="/#">{leagueUnitRating.leagueUnitName}</a></td>
            <td className="value">{leagueUnitRating.hatStats}</td>
            <td className="value">{leagueUnitRating.midfield * 3}</td>
            <td className="value">{leagueUnitRating.defense}</td>
            <td className="value">{leagueUnitRating.attack}</td>
        </tr>
    }    
}

export default LeagueUnits;