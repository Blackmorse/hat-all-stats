import LevelData from '../../../rest/models/leveldata/LevelData';
import TableSection, { SortingState } from '../../sections/TableSection';
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

abstract class PlayerCardsTable<Data extends LevelData, TableProps extends LevelDataProps<Data>> 
    extends TableSection<Data, TableProps, PlayerCards> {

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
                <th>{t('table.player')}</th>
                <th>{t('table.team')}</th>
                <th className="value">{t('table.league')}</th>
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
        return <tr key={"player_cards_row" + index}>
            <td>{index + 1}</td>
            <td>{playerSortingKey.firstName + ' ' + playerSortingKey.lastName}</td>
            <td><TeamLink id={playerSortingKey.teamId} name={playerSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={playerSortingKey.leagueUnitId} name={playerSortingKey.leagueUnitName} /></td>
            <td className="value">{playerCards.games}</td>
            <td className="value">{playerCards.playedMinutes}</td>
            <td className="value">{yellowCards(playerCards.yellowCards)}</td>
            <td className="value">{redCards(playerCards.redCards)}</td>
        </tr>
    }
}

export default PlayerCardsTable