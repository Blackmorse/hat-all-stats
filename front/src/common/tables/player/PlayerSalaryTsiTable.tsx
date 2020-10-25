import React from 'react';
import LevelData from "../../../rest/models/LevelData";
import ModelTable, { ModelTableProps, ModelTablePropsWrapper, SortingState } from "../../ModelTable";
import PlayerSalaryTSI from "../../../rest/models/player/PlayerSalaryTSI";
import { StatsTypeEnum } from "../../../rest/StatisticsParameters";
import { getPlayerSalaryTsi } from '../../../rest/Client';
import { Translation } from "react-i18next";
import ModelTableTh from "../../ModelTableTh";
import TeamLink from "../../links/TeamLink";
import LeagueUnitLink from "../../links/LeagueUnitLink";
import { ageFormatter, commasSeparated } from '../../Formatters'

abstract class PlayerSalaryTsiTable<Data extends LevelData, TableProps extends ModelTableProps<Data>> 
    extends ModelTable<Data, TableProps, PlayerSalaryTSI> {

    constructor(props: ModelTablePropsWrapper<Data, TableProps>) {
        super(props, 'tsi', {statType: StatsTypeEnum.ROUND, roundNumber: props.modelTableProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getPlayerSalaryTsi

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
                <ModelTableTh title='table.tsi' sortingField='tsi' sortingState={sortingState} />
                <ModelTableTh title='table.salary' titlePostfix={', ' + this.props.modelTableProps.currency()} sortingField='salary' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, playerSalaryTSI: PlayerSalaryTSI): JSX.Element {
        let playerSortingKey = playerSalaryTSI.playerSortingKey
        return <tr key={"player_cards_row" + index}>
            <td>{index + 1}</td>
            <td>{playerSortingKey.firstName + ' ' + playerSortingKey.lastName}</td>
            <td><TeamLink id={playerSortingKey.teamId} name={playerSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={playerSortingKey.leagueUnitId} name={playerSortingKey.leagueUnitName} /></td>
            <td className="value">{ageFormatter(playerSalaryTSI.age)}</td>
            <td className="value">{commasSeparated(playerSalaryTSI.tsi)}</td>
            <td className="value">{commasSeparated(playerSalaryTSI.salary / this.props.modelTableProps.currencyRate())}</td>
        </tr>
    }
}

export default PlayerSalaryTsiTable