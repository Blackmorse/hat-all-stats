import { type JSX } from 'react';
import '../../i18n'
import { Translation } from 'react-i18next'
import OverviewTableSection, { OverviewTableSectionProps } from './OverviewTableSection'
import PlayerStatOverview from '../../rest/models/overview/PlayerStatOverview'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'
import PlayerLink from '../links/PlayerLink'

abstract class PlayerOverviewSection extends OverviewTableSection<PlayerStatOverview> {
    valueTitle: string
    constructor(props: OverviewTableSectionProps<PlayerStatOverview>, valueTitle: string) {
        super(props)
        this.valueTitle = valueTitle
    }

    tableheader(): JSX.Element {
        return  <Translation>
            {t => <tr>{(this.isWorldData) ? <th></th> : <></>}
                    <th>{t('table.league')}</th>
                    <th>{t('table.team')}</th>

                    <th>{t('table.player')}</th>
                    <th>{this.valueTitle}</th>
                </tr>
            } 
            </Translation>
    }

    tableRow(playerStat: PlayerStatOverview, leagueNameFunc: (id: number) => JSX.Element | undefined): JSX.Element {
        
        return <tr key={'player_overview_' + playerStat.playerSortingKey.playerId}>
            {leagueNameFunc(playerStat.leagueId)}
            <td>
                <LeagueUnitLink id={playerStat.playerSortingKey.leagueUnitId} text={playerStat.playerSortingKey.leagueUnitName} />
            </td>
            <td>
                <TeamLink id={playerStat.playerSortingKey.teamId} text={playerStat.playerSortingKey.teamName} />
            </td>
            <td>
                <PlayerLink
                    id={playerStat.playerSortingKey.playerId}
                    text={playerStat.playerSortingKey.firstName + ' ' + playerStat.playerSortingKey.lastName}
                    externalLink
                    nationality={playerStat.playerSortingKey.nationality}
                    countriesMap={this.props.levelDataProps.countriesMap()}
                />
            </td>
            <td>
                {this.props.linkProvider(this.valueFormatter(playerStat.value), this.state.dataRequest.season, this.state.dataRequest.round, playerStat).render()}
            </td>
        </tr>
    }
}

export default PlayerOverviewSection
