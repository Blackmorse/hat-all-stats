import React from 'react';
import LevelData from "../../../rest/models/leveldata/LevelData";
import TableSection, { SortingState } from "../../sections/TableSection";
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import { Translation } from "react-i18next";
import '../../../i18n'
import PlayerInjury from '../../../rest/models/player/PlayerInjury';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import { getPlayerInjuries } from '../../../rest/Client';
import ModelTableTh from "../../elements/SortingTableTh";
import LeagueUnitLink from "../../links/LeagueUnitLink";
import TeamLink from "../../links/TeamLink";
import { injuryFormatter, ageFormatter } from '../../Formatters'
import ExternalPlayerLink from '../../links/ExternalPlayerLink';
import LeagueLink from '../../links/LeagueLink';
import CountryImage from '../../elements/CountryImage';

abstract class PlayerInjuriesTable<Data extends LevelData, TableProps extends LevelDataProps<Data>>
        extends TableSection<Data, TableProps, PlayerInjury>{
    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'injury', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getPlayerInjuries

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
                <ModelTableTh title='table.age' sortingField='age' sortingState={sortingState} />
                <ModelTableTh title='table.injury' sortingField='injury' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, playerInjury: PlayerInjury): JSX.Element {
        let playerSortingKey = playerInjury.playerSortingKey
        return <>
            <td>{index + 1}</td>
            <td className="value"><LeagueLink forceRefresh={true} id={playerSortingKey.nationality} text={<CountryImage countryId={playerSortingKey.nationality} text={this.props.levelDataProps.countriesMap().get(playerSortingKey.nationality)}/>} /></td>
            <td>{playerSortingKey.firstName + ' ' + playerSortingKey.lastName} <ExternalPlayerLink id={playerSortingKey.playerId}/></td>
            <td><TeamLink id={playerSortingKey.teamId} text={playerSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={playerSortingKey.leagueUnitId} text={playerSortingKey.leagueUnitName} /></td>
            <td className="value">{ageFormatter(playerInjury.age)}</td>
            <td className="value">{injuryFormatter(playerInjury.injury)}</td>
        </>
    }
}

export default PlayerInjuriesTable