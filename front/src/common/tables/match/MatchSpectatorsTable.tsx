import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps' 
import React from 'react';
import MatchSpectators from '../../../rest/models/match/MatchSpectators';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../elements/SortingTableTh'
import { getMatchSpectators } from '../../../rest/Client';
import MatchSpectatorsRow from '../rows/match/MatchSpectatorsRow'
import HattidTooltip from '../../elements/HattidTooltip';

abstract class MatchSpectatorsTable<TableProps extends LevelDataProps>
    extends ClassicTableSection<TableProps, MatchSpectators> {

    constructor(props: LevelDataPropsWrapper<TableProps>) {
        super(props, 'sold_total', {statType: StatsTypeEnum.ACCUMULATE},
            [StatsTypeEnum.ACCUMULATE, StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getMatchSpectators

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
                <th className="value"></th>
                <th className="text-center">{t('table.team')}</th>
                <ModelTableTh title='matches.spectatos' sortingField='sold_total' sortingState={sortingState} />
            </tr>
            }
        </Translation>
    }

    row(index: number, className: string, matchSpectators: MatchSpectators): JSX.Element {
        return <MatchSpectatorsRow key={this.constructor.name + '_' + matchSpectators.matchId } rowIndex={index} className={className} 
            rowModel={matchSpectators}/>
    }
}

export default MatchSpectatorsTable
