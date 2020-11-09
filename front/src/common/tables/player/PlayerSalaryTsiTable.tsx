import React from 'react';
import LevelData from "../../../rest/models/leveldata/LevelData";
import TableSection, { SortingState } from "../../sections/TableSection";
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import PlayerSalaryTSI from "../../../rest/models/player/PlayerSalaryTSI";
import { StatsTypeEnum } from "../../../rest/models/StatisticsParameters";
import { getPlayerSalaryTsi } from '../../../rest/Client';
import { Translation } from "react-i18next";
import ModelTableTh from "../../elements/SortingTableTh";
import TeamLink from "../../links/TeamLink";
import LeagueUnitLink from "../../links/LeagueUnitLink";
import { ageFormatter, commasSeparated } from '../../Formatters'

abstract class PlayerSalaryTsiTable<Data extends LevelData, TableProps extends LevelDataProps<Data>> 
    extends TableSection<Data, TableProps, PlayerSalaryTSI> {

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
                <th>{t('table.player')}</th>
                <th>{t('table.team')}</th>
                <th className="value">{t('table.league')}</th>
                <ModelTableTh title='table.age' sortingField='age' sortingState={sortingState} />
                <ModelTableTh title='table.tsi' sortingField='tsi' sortingState={sortingState} />
                <ModelTableTh title='table.salary' titlePostfix={', ' + this.props.levelDataProps.currency()} sortingField='salary' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, playerSalaryTSI: PlayerSalaryTSI): JSX.Element {
        let playerSortingKey = playerSalaryTSI.playerSortingKey
        return <tr key={"player_cards_row" + index}>
            <td>{index + 1}</td>
            <td>{playerSortingKey.firstName + ' ' + playerSortingKey.lastName}</td>
            <td><TeamLink id={playerSortingKey.teamId} text={playerSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={playerSortingKey.leagueUnitId} text={playerSortingKey.leagueUnitName} /></td>
            <td className="value">{ageFormatter(playerSalaryTSI.age)}</td>
            <td className="value">{commasSeparated(playerSalaryTSI.tsi)}</td>
            <td className="value">{commasSeparated(playerSalaryTSI.salary / this.props.levelDataProps.currencyRate())}</td>
        </tr>
    }
}

export default PlayerSalaryTsiTable