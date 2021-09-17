import React from 'react';
import MatchTopHatstats from '../../rest/models/match/MatchTopHatstats'
import '../../i18n'
import { Translation } from 'react-i18next'
import LevelData from '../../rest/models/leveldata/LevelData'
import OverviewTableSection from './OverviewTableSection'
import MatchOverviewRow from '../tables/rows/match/MatchOverviewRow'

abstract class MatchesOverviewSection<Data extends LevelData> extends OverviewTableSection<Data, MatchTopHatstats> {

    tableheader(): JSX.Element {
        return <Translation>
            {(t, { i18n}) =>  <tr>
                        {(this.isWorldData)  ? <th className="value"></th> : <></>}
                        <th className="value">{t('table.league')}</th>
                        <th className="value">{t('table.team')}</th>
                        <th className="value">{t('table.loddar_stats')}</th>
                        <th className="value">{t('table.hatstats')}</th>
                        <th></th>
                        <th className="value">{t('table.hatstats')}</th>
                        <th className="value">{t('table.loddar_stats')}</th>
                        <th className="value">{t('table.team')}</th>
                        <th/>
                    </tr>
            }
            </Translation>
    }
    
    tableRow(matc: MatchTopHatstats, leagueNameFunc: (id: number) => JSX.Element): JSX.Element {
        return <MatchOverviewRow rowIndex={0} rowModel={matc} request={this.state.dataRequest}
            linkProvider={this.props.linkProvider} leagueNameFunc={leagueNameFunc} className="" 
            key={'match_overview_section_' + matc.matchId}/>
    }
}

export default MatchesOverviewSection