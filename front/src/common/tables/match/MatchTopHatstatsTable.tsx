import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import React from 'react';
import MatchTopHatstats from '../../../rest/models/match/MatchTopHatstats';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../elements/SortingTableTh'
import { getMatchesTopHatstats } from '../../../rest/Client';
import MatchTopHatstatsRow from '../rows/match/MatchTopHatstatsRow'
import HattidTooltip from '../../elements/HattidTooltip';

abstract class MatchTopHatstatsTable<TableProps extends LevelDataProps>
    extends ClassicTableSection<TableProps, MatchTopHatstats> {
    
    constructor(props: LevelDataPropsWrapper<TableProps>) {
        super(props, 'sum_hatstats', {statType: StatsTypeEnum.ACCUMULATE},
            [StatsTypeEnum.ACCUMULATE, StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getMatchesTopHatstats

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            t =>
            <tr>
                <th/>
                <HattidTooltip 
                    poppedHint={t('table.position')}
                    content={<th>{t('table.position_abbr')}</th>}
                />
                <th className="text-center">{t('table.league')}</th>
                <th className="text-center">{t('table.team')}</th>
                <ModelTableTh title='table.loddar_stats' sorting={{field: 'sum_loddar_stats', state: sortingState}} />
                <ModelTableTh title='table.hatstats' sorting={{field: 'sum_hatstats', state: sortingState}} />
                <th></th>
                <ModelTableTh title='table.hatstats' sorting={{field: 'sum_hatstats', state: sortingState}} />
                <ModelTableTh title='table.loddar_stats' sorting={{field: 'sum_loddar_stats', state: sortingState}} />
                <th className="text-center">{t('table.team')}</th>
            </tr>
            }
        </Translation>
    }

    row(index: number, className: string, matchHatstats: MatchTopHatstats): JSX.Element {
        return <MatchTopHatstatsRow key={this.constructor.name + '_' + matchHatstats.matchId } rowIndex={index} 
            className={className} rowModel={matchHatstats} />
    }
}

export default MatchTopHatstatsTable
