import React from 'react';
import MatchTopHatstatsOverview from '../../rest/models/overview/MatchTopHatstatsOverview'
import '../../i18n'
import { Translation } from 'react-i18next'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'
import LevelData from '../../rest/models/leveldata/LevelData';
import OverviewTableSection from './OverviewTableSection';

abstract class MatchesOverviewSection<Data extends LevelData> extends OverviewTableSection<Data, MatchTopHatstatsOverview> {

    tableheader(): JSX.Element {
        return <Translation>
            {(t, { i18n}) =>  <tr>
                        {(this.isWorldData)  ? <th className="value">{t('overview.country')}</th> : <></>}
                        <th className="value">{t('table.league')}</th>
                        <th className="value">{t('table.team')}</th>
                        <th className="value">{t('table.hatstats')}</th>
                        <th></th>
                        <th className="value">{t('table.hatstats')}</th>
                        <th className="value">{t('table.team')}</th>
                    </tr>
            }
            </Translation>
    }
    
    tableRow(matc: MatchTopHatstatsOverview, leagueNameFunc: (id: number) => JSX.Element): JSX.Element {
        return <tr>
            {leagueNameFunc(matc.leagueId)}
            <td className="value">
                <LeagueUnitLink id={matc.matchTopHatstats.homeTeam.leagueUnitId} text={matc.matchTopHatstats.homeTeam.leagueUnitName} />
            </td>
            <td className="value">
                <TeamLink id={matc.matchTopHatstats.homeTeam.teamId} text={matc.matchTopHatstats.homeTeam.teamName}/>
                </td>
            <td className="value">
                {this.props.linkProvider(matc.matchTopHatstats.homeHatstats.toString(), this.state.selectedSeason, this.state.selectedRound, matc).render()}
            </td>
            <td className="value">
                {matc.matchTopHatstats.homeGoals} : {matc.matchTopHatstats.awayGoals}
            </td>
            <td className="value">
            {this.props.linkProvider(matc.matchTopHatstats.awayHatstats.toString(), this.state.selectedSeason, this.state.selectedRound, matc).render()}
            </td>
            <td className="value"><TeamLink id={matc.matchTopHatstats.awayTeam.teamId} text={matc.matchTopHatstats.awayTeam.teamName}/></td>
        </tr>
    }
}

export default MatchesOverviewSection