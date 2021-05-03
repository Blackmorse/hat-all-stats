import React from 'react';
import LevelData from "../../../rest/models/leveldata/LevelData";
import { SortingState } from '../AbstractTableSection'
import PlayersTableSection from '../PlayersTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import PlayerSalaryTSI from "../../../rest/models/player/PlayerSalaryTSI";
import { StatsTypeEnum } from "../../../rest/models/StatisticsParameters";
import { getPlayerSalaryTsi } from '../../../rest/Client';
import { Translation } from "react-i18next";
import ModelTableTh from "../../elements/SortingTableTh";
import TeamLink from "../../links/TeamLink";
import LeagueUnitLink from "../../links/LeagueUnitLink";
import { ageFormatter, commasSeparated, salaryFormatter } from '../../Formatters'
import ExternalPlayerLink from '../../links/ExternalPlayerLink';
import LeagueLink from '../../links/LeagueLink';
import CountryImage from '../../elements/CountryImage';
import Mappings from '../../enums/Mappings';
import i18n from '../../../i18n';

abstract class PlayerSalaryTsiTable<Data extends LevelData, TableProps extends LevelDataProps<Data>> 
    extends PlayersTableSection<Data, TableProps, PlayerSalaryTSI> {

    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
        super(props, 'tsi', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getPlayerSalaryTsi

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
                <ModelTableTh title='table.tsi' sortingField='tsi' sortingState={sortingState} />
                <ModelTableTh title='table.salary' titlePostfix={', ' + this.props.levelDataProps.currency()} sortingField='salary' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, playerSalaryTSI: PlayerSalaryTSI): JSX.Element {
        let playerSortingKey = playerSalaryTSI.playerSortingKey
        return <>
            <td>{index + 1}</td>
            <td className="value"><LeagueLink forceRefresh={true} id={playerSortingKey.nationality} text={<CountryImage countryId={playerSortingKey.nationality} text={this.props.levelDataProps.countriesMap().get(playerSortingKey.nationality)}/>} /></td>
            <td>{playerSortingKey.firstName + ' ' + playerSortingKey.lastName} <ExternalPlayerLink id={playerSortingKey.playerId}/></td>
            <td><TeamLink id={playerSortingKey.teamId} text={playerSortingKey.teamName} 
               flagCountryNumber={this.props.showCountryFlags !== undefined && this.props.showCountryFlags ? playerSortingKey.teamLeagueId : undefined} /></td>
            <td className="value"><LeagueUnitLink id={playerSortingKey.leagueUnitId} text={playerSortingKey.leagueUnitName} /></td>
            <td className="value">{i18n.t(Mappings.roleToTranslationMap.get(playerSalaryTSI.role) || '')}</td>
            <td className="value">{ageFormatter(playerSalaryTSI.age)}</td>
            <td className="value">{commasSeparated(playerSalaryTSI.tsi)}</td>
            <td className="value">{salaryFormatter(playerSalaryTSI.salary, this.props.levelDataProps.currencyRate())}</td>
        </>
    }
}

export default PlayerSalaryTsiTable