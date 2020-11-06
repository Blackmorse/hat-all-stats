import React from 'react';
import LevelData from "../../../rest/models/leveldata/LevelData";
import ModelTable, { ModelTableProps, ModelTablePropsWrapper, SortingState } from "../../ModelTable";
import { Translation } from "react-i18next";
import '../../../i18n'
import PlayerInjury from '../../../rest/models/player/PlayerInjury';
import { StatsTypeEnum } from '../../../rest/StatisticsParameters';
import { getPlayerInjuries } from '../../../rest/Client';
import ModelTableTh from "../../ModelTableTh";
import LeagueUnitLink from "../../links/LeagueUnitLink";
import TeamLink from "../../links/TeamLink";
import { injuryFormatter, ageFormatter } from '../../Formatters'

abstract class PlayerInjuriesTable<Data extends LevelData, TableProps extends ModelTableProps<Data>>
        extends ModelTable<Data, TableProps, PlayerInjury>{
    constructor(props: ModelTablePropsWrapper<Data, TableProps>) {
        super(props, 'injury', {statType: StatsTypeEnum.ROUND, roundNumber: props.modelTableProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getPlayerInjuries

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
                <ModelTableTh title='table.injury' sortingField='injury' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, playerInjury: PlayerInjury): JSX.Element {
        let playerSortingKey = playerInjury.playerSortingKey
        return <tr key={"player_ratings_row" + index}>
            <td>{index + 1}</td>
            <td>{playerSortingKey.firstName + ' ' + playerSortingKey.lastName}</td>
            <td><TeamLink id={playerSortingKey.teamId} name={playerSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={playerSortingKey.leagueUnitId} name={playerSortingKey.leagueUnitName} /></td>
            <td className="value">{ageFormatter(playerInjury.age)}</td>
            <td className="value">{injuryFormatter(playerInjury.injury)}</td>
        </tr>
    }
}

export default PlayerInjuriesTable