import React from 'react';
import LevelData from "../../../rest/models/LevelData";
import ModelTable, { ModelTableProps, ModelTablePropsWrapper, SortingState } from "../../ModelTable";
import { StatsTypeEnum } from "../../../rest/StatisticsParameters";
import { getPlayerRatings } from '../../../rest/Client';
import PlayerRating from "../../../rest/models/player/PlayerRating";
import { Translation } from "react-i18next";
import '../../../i18n'
import ModelTableTh from "../../ModelTableTh";
import TeamLink from "../../links/TeamLink";
import LeagueUnitLink from "../../links/LeagueUnitLink";
import { ageFormatter, ratingFormatter } from '../../Formatters'

abstract class PlayerRatingsTable<Data extends LevelData, TableProps extends ModelTableProps<Data>> 
        extends ModelTable<Data, TableProps, PlayerRating> {
    
    constructor(props: ModelTablePropsWrapper<Data, TableProps>) {
        super(props, 'rating', {statType: StatsTypeEnum.ROUND, roundNumber: props.modelTableProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getPlayerRatings

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th>{t('table.player')}</th>
                <th>{t('table.team')}</th>
                <th className="value">{t('table.league')}</th>
                <ModelTableTh title='table.age' sortingField='age' sortingState={sortingState} />
                <ModelTableTh title='table.rating' sortingField='rating' sortingState={sortingState} />
                <ModelTableTh title='table.rating_end_of_match' sortingField='rating_end_of_match' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, playerRating: PlayerRating): JSX.Element {
        let playerSortingKey = playerRating.playerSortingKey
        return <tr key={"player_ratings_row" + index}>
            <td>{index + 1}</td>
            <td>{playerSortingKey.firstName + ' ' + playerSortingKey.lastName}</td>
            <td><TeamLink id={playerSortingKey.teamId} name={playerSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={playerSortingKey.leagueUnitId} name={playerSortingKey.leagueUnitName} /></td>
            <td className="value">{ageFormatter(playerRating.age)}</td>
            <td className="value">{ratingFormatter(playerRating.rating)}</td>
            <td className="value">{ratingFormatter(playerRating.ratingEndOfMatch)}</td>
        </tr>
    }
}

export default PlayerRatingsTable