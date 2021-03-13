import React from 'react';
import MatchTopHatstats from '../../rest/models/match/MatchTopHatstats'
import '../../i18n'
import { Translation } from 'react-i18next'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'
import LevelData from '../../rest/models/leveldata/LevelData'
import OverviewTableSection from './OverviewTableSection'
import ExternalMatchLink from '../links/ExternalMatchLink'
import { loddarStats } from '../Formatters'

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
                    </tr>
            }
            </Translation>
    }
    
    tableRow(matc: MatchTopHatstats, leagueNameFunc: (id: number) => JSX.Element): JSX.Element {
        return <tr key={'match_overview_section_' + matc.matchId}>
            {leagueNameFunc(matc.homeTeam.leagueId)}
            <td className="value">
                <LeagueUnitLink id={matc.homeTeam.leagueUnitId} text={matc.homeTeam.leagueUnitName} />
            </td>
            <td className="value">
                <TeamLink id={matc.homeTeam.teamId} text={matc.homeTeam.teamName}/>
                </td>
            <td className="value">
                {this.props.linkProvider(loddarStats(matc.homeLoddarStats), this.state.dataRequest.season, this.state.dataRequest.round, matc).render()}
            </td>
            <td className="value">
                {this.props.linkProvider(matc.homeHatstats.toString(), this.state.dataRequest.season, this.state.dataRequest.round, matc).render()}
            </td>
            <td className="value">
                {matc.homeGoals} : {matc.awayGoals} <ExternalMatchLink id={matc.matchId}/>
            </td>
            <td className="value">
                {this.props.linkProvider(matc.awayHatstats.toString(), this.state.dataRequest.season, this.state.dataRequest.round, matc).render()}
            </td>
            <td className="value">
                {this.props.linkProvider(loddarStats(matc.awayLoddarStats), this.state.dataRequest.season, this.state.dataRequest.round, matc).render()}
            </td>
            <td className="value"><TeamLink id={matc.awayTeam.teamId} text={matc.awayTeam.teamName}/></td>
        </tr>
    }
}

export default MatchesOverviewSection