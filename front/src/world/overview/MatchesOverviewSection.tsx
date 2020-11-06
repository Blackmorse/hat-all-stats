import React from 'react';
import MatchTopHatstatsOverview from '../../rest/models/overview/MatchTopHatstatsOverview'
import '../../i18n'
import { Translation } from 'react-i18next'
import WorldData from '../../rest/models/leveldata/WorldData';
import OverviewSection from './OverviewSection'
import LeagueLink from '../../common/links/LeagueLink'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'

abstract class MatchesOverviewSection extends OverviewSection<WorldData, Array<MatchTopHatstatsOverview>> {
    renderOverviewSection(matches: Array<MatchTopHatstatsOverview>): JSX.Element {
        let nameMap = new Map(this.props.levelDataProps.levelData.countries)
        return <Translation>
            {(t, { i18n}) => <table className="statistics_table">
                <thead>
                    <tr>
                        <th className="value">{t('overview.country')}</th>
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
                    <td className="value">
                        <LeagueLink tableLink={true} id={matc.leagueId} name={nameMap.get(matc.leagueId) || ''}/>
                    </td>
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