import React from 'react';
import MatchTopHatstatsOverview from '../../rest/models/overview/MatchTopHatstatsOverview'
import '../../i18n'
import { Translation } from 'react-i18next'
import OverviewSection from './OverviewSection'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'
import LevelData from '../../rest/models/leveldata/LevelData';

abstract class MatchesOverviewSection<Data extends LevelData> extends OverviewSection<Data, Array<MatchTopHatstatsOverview>> {
    renderOverviewSection(matches: Array<MatchTopHatstatsOverview>, leagueNameFunc: (id: number) => JSX.Element): JSX.Element {
       
        return <Translation>
            {(t, { i18n}) => <table className="statistics_table">
                <thead>
                    <tr>
                        {(this.isWorldData)  ? <th className="value">{t('overview.country')}</th> : <></>}
                        <th className="value">{t('table.league')}</th>
                        <th className="value">{t('table.team')}</th>
                        <th className="value">{t('table.hatstats')}</th>
                        <th></th>
                        <th className="value">{t('table.hatstats')}</th>
                        <th className="value">{t('table.team')}</th>
                    </tr>
                </thead>
                <tbody>
                {matches.map(matc => {
                return <tr>
                    {leagueNameFunc(matc.leagueId)}
                    <td className="value">
                        <LeagueUnitLink id={matc.matchTopHatstats.homeTeam.leagueUnitId} name={matc.matchTopHatstats.homeTeam.leagueUnitName} />
                    </td>
                    <td className="value">
                        <TeamLink id={matc.matchTopHatstats.homeTeam.teamId} name={matc.matchTopHatstats.homeTeam.teamName}/>
                        </td>
                    <td className="value">
                        {matc.matchTopHatstats.homeHatstats}
                    </td>
                    <td className="value">
                        {matc.matchTopHatstats.homeGoals} : {matc.matchTopHatstats.awayGoals}
                    </td>
                    <td className="value">
                        {matc.matchTopHatstats.awayHatstats}
                    </td>
                    <td className="value"><TeamLink id={matc.matchTopHatstats.awayTeam.teamId} name={matc.matchTopHatstats.awayTeam.teamName}/></td>
                </tr>
                })}
                </tbody>
            </table>

        }
        </Translation>
    }
}

export default MatchesOverviewSection