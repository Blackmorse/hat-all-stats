import React from 'react'
import WorldData from '../../rest/models/leveldata/WorldData';
import '../../i18n'
import { Translation } from 'react-i18next'
import OverviewSection, { OverviewSectionProps } from './OverviewSection'
import PlayerStatOverview from '../../rest/models/overview/PlayerStatOverview'
import LeagueLink from '../../common/links/LeagueLink'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'

abstract class PlayerOverviewSection extends OverviewSection<WorldData, Array<PlayerStatOverview>> {
    valueTitle: string
    constructor(props: OverviewSectionProps<WorldData, Array<PlayerStatOverview>>, 
            title: string,
            valueTitle: string) {
        super(props, title)
        this.valueTitle = valueTitle
    }

    abstract valueFormatter(value: number): JSX.Element

    renderOverviewSection(playerStats: Array<PlayerStatOverview>): JSX.Element {
        let map = new Map(this.props.levelDataProps.levelData.countries)
        return <Translation>
        {(t, { i18n}) => <table className="statistics_table">
            <thead>
                <tr>
                    <th className="value">{t('overview.country')}</th>
                    <th className="value">{t('table.league')}</th>
                    <th className="value">{t('table.team')}</th>
                    <th className="value">{t('table.player')}</th>
                    <th className="value">{this.valueTitle}</th>
                </tr>
            </thead>
            <tbody>
                {playerStats.map(playerStat =>{
                    return <tr>
                    <td className="value">
                        <LeagueLink tableLink={true} id={playerStat.leagueId} name={map.get(playerStat.leagueId) || ''} />
                    </td>
                    <td className="value">
                        <LeagueUnitLink id={playerStat.playerSortingKey.leagueUnitId} name={playerStat.playerSortingKey.leagueUnitName} />
                    </td>
                    <td className="value">
                        <TeamLink id={playerStat.playerSortingKey.teamId} name={playerStat.playerSortingKey.teamName} />
                    </td>
                    <td className="value">{playerStat.playerSortingKey.firstName} {playerStat.playerSortingKey.lastName}</td>
                    <td className="value">
                        {this.valueFormatter(playerStat.value)}
                    </td>
                </tr>})}
            </tbody>
        </table>
        }
        </Translation>
    }
}

export default PlayerOverviewSection