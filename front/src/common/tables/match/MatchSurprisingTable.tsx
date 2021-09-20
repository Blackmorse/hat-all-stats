import LevelData from '../../../rest/models/leveldata/LevelData';
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import React from 'react';
import MatchTopHatstats from '../../../rest/models/match/MatchTopHatstats';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../elements/SortingTableTh'
import { getSurprisingMatches } from '../../../rest/Client';
import MatchSurprisingRow from '../rows/match/MatchSurprisingRow'

abstract class MatchSurprisingTable<Data extends LevelData, TableProps extends LevelDataProps<Data>>
    extends ClassicTableSection<Data, TableProps, MatchTopHatstats> {
    
    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'abs_hatstats_difference', {statType: StatsTypeEnum.ACCUMULATE},
            [StatsTypeEnum.ACCUMULATE, StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getSurprisingMatches

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th className="value">{t('table.league')}</th>
                <th className="value">{t('table.team')}</th>
                <ModelTableTh title='table.loddar_stats' sortingField='abs_loddar_stats_difference' sortingState={sortingState} />
                <ModelTableTh title='table.hatstats' sortingField='abs_hatstats_difference' sortingState={sortingState} />
                <ModelTableTh title='overview.goals' sortingField='abs_goals_difference' sortingState={sortingState} />
                <ModelTableTh title='table.hatstats' sortingField='abs_hatstats_difference' sortingState={sortingState} />
                <ModelTableTh title='table.loddar_stats' sortingField='abs_loddar_stats_difference' sortingState={sortingState} />
                <th className="value">{t('table.team')}</th>
                <th/>
            </tr>
            }
        </Translation>
    }

    row(index: number, className: string, matchHatstats: MatchTopHatstats): JSX.Element {
        return <MatchSurprisingRow key={this.constructor.name + '_' + matchHatstats.matchId } rowIndex={index} 
            rowModel={matchHatstats} className={className} />
    }
}

export default MatchSurprisingTable