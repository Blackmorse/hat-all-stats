import LevelData from '../../../rest/models/leveldata/LevelData';
import { SortingState } from '../AbstractTableSection'
import PlayersTableSection from '../PlayersTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import React from 'react';
import PlayerCards from '../../../rest/models/player/PlayerCards';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../elements/SortingTableTh'
import TeamLink from '../../links/TeamLink'
import LeagueUnitLink from '../../links/LeagueUnitLink'
import { yellowCards, redCards } from '../../Formatters'
import { getPlayerCards } from '../../../rest/Client';
import ExternalPlayerLink from '../../links/ExternalPlayerLink';
import CountryImage from '../../elements/CountryImage';
import LeagueLink from '../../links/LeagueLink';
import Mappings from '../../enums/Mappings';
import i18n from '../../../i18n';
import { ageFormatter } from '../../Formatters'

abstract class PlayerCardsTable<Data extends LevelData, TableProps extends LevelDataProps<Data>> 
    extends PlayersTableSection<Data, TableProps, PlayerCards> {

    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'yellow_cards', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getPlayerCards

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th></th>
                <th>{t('table.player')}</th>
                <th>{t('table.team')}</th>
                <th className="value">{t('table.league')}</th>
                <th className="value"></th>
                <th>{t('table.age')}</th>
                <ModelTableTh title='table.games' sortingField='games' sortingState={sortingState} />
                <ModelTableTh title='table.minutes' sortingField='played' sortingState={sortingState} />
                <ModelTableTh title='table.yellow_cards' sortingField='yellow_cards' sortingState={sortingState} />
                <ModelTableTh title='table.red_cards' sortingField='red_cards' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, playerCards: PlayerCards): JSX.Element {
        let playerSortingKey = playerCards.playerSortingKey
        return <>
            <td>{index + 1}</td>
            <td className="value"><LeagueLink forceRefresh={true} id={playerSortingKey.nationality} text={<CountryImage countryId={playerSortingKey.nationality} text={this.props.levelDataProps.countriesMap().get(playerSortingKey.nationality)}/>} /></td>
            <td>{playerSortingKey.firstName + ' ' + playerSortingKey.lastName} <ExternalPlayerLink id={playerSortingKey.playerId} /></td>
            <td><TeamLink id={playerSortingKey.teamId} text={playerSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={playerSortingKey.leagueUnitId} text={playerSortingKey.leagueUnitName} /></td>
            <td className="value">{i18n.t(Mappings.roleToTranslationMap.get(playerCards.role) || '')}</td>
            <td className="value">{ageFormatter(playerCards.age)}</td>
            <td className="value">{playerCards.games}</td>
            <td className="value">{playerCards.playedMinutes}</td>
            <td className="value">{yellowCards(playerCards.yellowCards)}</td>
            <td className="value">{redCards(playerCards.redCards)}</td>
        </>
    }
}

export default PlayerCardsTable