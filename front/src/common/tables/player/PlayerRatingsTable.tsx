import React from 'react';
import LevelData from "../../../rest/models/leveldata/LevelData";
import { SortingState } from '../AbstractTableSection'
import PlayersTableSection from '../PlayersTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import { StatsTypeEnum } from "../../../rest/models/StatisticsParameters";
import { getPlayerRatings } from '../../../rest/Client';
import PlayerRating from "../../../rest/models/player/PlayerRating";
import { Translation } from "react-i18next";
import '../../../i18n'
import ModelTableTh from "../../elements/SortingTableTh";
import TeamLink from "../../links/TeamLink";
import LeagueUnitLink from "../../links/LeagueUnitLink";
import { ageFormatter, ratingFormatter } from '../../Formatters'
import ExternalPlayerLink from '../../links/ExternalPlayerLink';
import LeagueLink from '../../links/LeagueLink';
import CountryImage from '../../elements/CountryImage';
import Mappings from '../../enums/Mappings';
import i18n from '../../../i18n';

abstract class PlayerRatingsTable<Data extends LevelData, TableProps extends LevelDataProps<Data>> 
        extends PlayersTableSection<Data, TableProps, PlayerRating> {
    
    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'rating', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getPlayerRatings

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
                <th></th>
                <ModelTableTh title='table.age' sortingField='age' sortingState={sortingState} />
                <ModelTableTh title='table.rating' sortingField='rating' sortingState={sortingState} />
                <ModelTableTh title='table.rating_end_of_match' sortingField='rating_end_of_match' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, playerRating: PlayerRating): JSX.Element {
        let playerSortingKey = playerRating.playerSortingKey
        return <>
            <td>{index + 1}</td>
            <td className="value"><LeagueLink forceRefresh={true} id={playerSortingKey.nationality} text={<CountryImage countryId={playerSortingKey.nationality} text={this.props.levelDataProps.countriesMap().get(playerSortingKey.nationality)}/>} /></td>
            <td>{playerSortingKey.firstName + ' ' + playerSortingKey.lastName} <ExternalPlayerLink id={playerSortingKey.playerId}/></td>
            <td><TeamLink id={playerSortingKey.teamId} text={playerSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={playerSortingKey.leagueUnitId} text={playerSortingKey.leagueUnitName} /></td>
            <td className="value">{i18n.t(Mappings.roleToTranslationMap.get(playerRating.role) || '')}</td>
            <td className="value">{ageFormatter(playerRating.age)}</td>
            <td className="value">{ratingFormatter(playerRating.rating)}</td>
            <td className="value">{ratingFormatter(playerRating.ratingEndOfMatch)}</td>
        </>
    }
}

export default PlayerRatingsTable