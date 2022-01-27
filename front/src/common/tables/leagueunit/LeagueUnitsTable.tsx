import React from 'react';
import LeagueUnitRating from '../../../rest/models/leagueunit/LeagueUnitRating'
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import ModelTableTh from '../../elements/SortingTableTh'
import { Translation } from 'react-i18next'
import '../../../i18n'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import LevelData from '../../../rest/models/leveldata/LevelData';
import LeagueUnitLink from '../../links/LeagueUnitLink'
import { getLeagueUnits } from '../../../rest/Client'
import { loddarStats } from '../../Formatters'
import HattidTooltip from '../../elements/HattidTooltip';

class LeagueUnitsTable<Data extends LevelData, TableProps extends LevelDataProps<Data>> 
        extends ClassicTableSection<Data, TableProps ,LeagueUnitRating> {

    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'hatstats', {statType: StatsTypeEnum.AVG}, 
            [StatsTypeEnum.AVG, StatsTypeEnum.MAX, StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getLeagueUnits

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>{
            (t, { i18n }) => <tr>
                <HattidTooltip 
                    poppedHint={t('table.position')}
                    content={<th>{t('table.position_abbr')}</th>              }
                />
                <th className="text-center">{t('table.league')}</th>
                <ModelTableTh title='table.hatstats' sortingField='hatstats'  sortingState={sortingState}/>
                <ModelTableTh title='table.midfield' sortingField='midfield' sortingState={sortingState}/>
                <ModelTableTh title='table.defense' sortingField='defense' sortingState={sortingState}/>
                <ModelTableTh title='table.attack' sortingField='attack' sortingState={sortingState}/>
                <ModelTableTh title='table.loddar_stats' sortingField='loddar_stats' sortingState={sortingState}/>
            </tr>
            }
            </Translation> 
    }

    row(index: number, className: string, leagueUnitRating: LeagueUnitRating): JSX.Element {
        return <tr className={className}>
            <td>{index + 1}</td>
            <td className="text-center"><LeagueUnitLink id={leagueUnitRating.leagueUnitId} text={leagueUnitRating.leagueUnitName}/></td>
            <td className="text-center">{leagueUnitRating.hatStats}</td>
            <td className="text-center">{leagueUnitRating.midfield * 3}</td>
            <td className="text-center">{leagueUnitRating.defense}</td>
            <td className="text-center">{leagueUnitRating.attack}</td>
            <td className="text-center">{loddarStats(leagueUnitRating.loddarStats)}</td>
        </tr>
    }    
}

export default LeagueUnitsTable;
