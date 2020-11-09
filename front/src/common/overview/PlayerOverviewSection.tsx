import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import OverviewSection, { OverviewSectionProps } from './OverviewSection'
import PlayerStatOverview from '../../rest/models/overview/PlayerStatOverview'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'
import LevelData from '../../rest/models/leveldata/LevelData';

abstract class PlayerOverviewSection<Data extends LevelData> extends OverviewSection<Data, Array<PlayerStatOverview>> {
    valueTitle: string
    constructor(props: OverviewSectionProps<Data, Array<PlayerStatOverview>>, 
            title: string,
            valueTitle: string) {
        super(props, title)
        this.valueTitle = valueTitle
    }

    abstract valueFormatter(value: number): JSX.Element

    renderOverviewSection(playerStats: Array<PlayerStatOverview>, leagueNameFunc: (id: number) => JSX.Element): JSX.Element {
        return <Translation>
        {(t, { i18n}) => <table className="statistics_table">
            <thead>
                <tr>
                    {(this.isWorldData) ? <th className="value">{t('overview.country')}</th> : <></>}
                    <th className="value">{t('table.league')}</th>
                    <th className="value">{t('table.team')}</th>
                    <th className="value">{t('table.player')}</th>
                    <th className="value">{this.valueTitle}</th>
                </tr>
            </thead>
            <tbody>
                {playerStats.map(playerStat =>{
                    return <tr>
                    {leagueNameFunc(playerStat.leagueId)}
                    <td className="value">
                        <LeagueUnitLink id={playerStat.playerSortingKey.leagueUnitId} text={playerStat.playerSortingKey.leagueUnitName} />
                    </td>
                    <td className="value">
                        <TeamLink id={playerStat.playerSortingKey.teamId} text={playerStat.playerSortingKey.teamName} />
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