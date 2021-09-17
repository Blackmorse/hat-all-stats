import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import OverviewTableSection, { OverviewTableSectionProps } from './OverviewTableSection'
import PlayerStatOverview from '../../rest/models/overview/PlayerStatOverview'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'
import LevelData from '../../rest/models/leveldata/LevelData';
import ExternalPlayerLink from '../links/ExternalPlayerLink'

abstract class PlayerOverviewSection<Data extends LevelData> extends OverviewTableSection<Data, PlayerStatOverview> {
    valueTitle: string
    constructor(props: OverviewTableSectionProps<Data, PlayerStatOverview>, valueTitle: string) {
        super(props)
        this.valueTitle = valueTitle
    }

    tableheader(): JSX.Element {
        return  <Translation>
            {(t, { i18n }) => <tr>{(this.isWorldData) ? <th className="value"></th> : <></>}
                    <th className="value">{t('table.league')}</th>
                    <th className="value">{t('table.team')}</th>

                    <th className="value">{t('table.player')}</th>
                    <th className="value">{this.valueTitle}</th>
                </tr>
            } 
            </Translation>
    }

    tableRow(playerStat: PlayerStatOverview, leagueNameFunc: (id: number) => JSX.Element | undefined): JSX.Element {
        
        return <tr key={'player_overview_' + playerStat.playerSortingKey.playerId}>
            {leagueNameFunc(playerStat.leagueId)}
            <td className="value">
                <LeagueUnitLink id={playerStat.playerSortingKey.leagueUnitId} text={playerStat.playerSortingKey.leagueUnitName} />
            </td>
            <td className="value">
                <TeamLink id={playerStat.playerSortingKey.teamId} text={playerStat.playerSortingKey.teamName} />
            </td>
            <td className="value">{playerStat.playerSortingKey.firstName} {playerStat.playerSortingKey.lastName} <ExternalPlayerLink id={playerStat.playerSortingKey.playerId} /></td>
            <td className="value">
                {this.props.linkProvider(this.valueFormatter(playerStat.value), this.state.dataRequest.season, this.state.dataRequest.round, playerStat).render()}
            </td>
        </tr>
    }
}

export default PlayerOverviewSection