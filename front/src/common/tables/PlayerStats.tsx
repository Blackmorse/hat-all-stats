import React from 'react';
import LevelData from '../../rest/models/leveldata/LevelData';
import ModelTable, { ModelTablePropsWrapper, SortingState, ModelTableProps } from '../ModelTable'
import { StatsTypeEnum } from '../../rest/StatisticsParameters';
import PlayerStat from '../../rest/models/PlayerStat'
import { Translation } from 'react-i18next'
import '../../i18n'
import ModelTableTh from '../ModelTableTh'
import { ageFormatter, yellowCards, redCards } from '../../common/Formatters'
import LeagueUnitLink from '../links/LeagueUnitLink'
import TeamLink from '../links/TeamLink'
import { getPlayerStats } from '../../rest/Client'

abstract class PlayerStats<Data extends LevelData, TableProps extends ModelTableProps<Data>> extends ModelTable<Data, TableProps, PlayerStat> {
    constructor(props: ModelTablePropsWrapper<Data, TableProps>) {
        super(props, 'scored', {statType: StatsTypeEnum.ACCUMULATE},
            [StatsTypeEnum.MAX, StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getPlayerStats

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>{
            (t, { i18n }) => <tr>
                    <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                    <th>{t('table.player')}</th>
                    <th>{t('table.team')}</th>
                    <th className="value">{t('table.league')}</th>
                    <ModelTableTh title='table.age' sortingField='age' sortingState={sortingState} />
                    <ModelTableTh title='table.games' sortingField='games' sortingState={sortingState} />
                    <ModelTableTh title='table.minutes_abbr' poppedHint={t('table.minutes')} sortingField='played' sortingState={sortingState} />
                    <ModelTableTh title='table.scored_abbr' poppedHint={t('table.scored')} sortingField='scored' sortingState={sortingState} />
                    <ModelTableTh title='table.yellow_cards_abbr' poppedHint={t('table.yellow_cards')} sortingField='yellow_cards' sortingState={sortingState} />
                    <ModelTableTh title='table.red_cards_abbr' poppedHint={t('table.red_cards')} sortingField='red_cards' sortingState={sortingState} />
                    <ModelTableTh title='table.injury_abbr' poppedHint={t('table.injury')} sortingField='total_injuries' sortingState={sortingState} />
                    <ModelTableTh title='table.minutes_per_goal_abbr' poppedHint={t('table.minutes_per_goal')} sortingField='goal_rate' sortingState={sortingState} />
                </tr>
        } 
        </Translation>
    }

    columnValues(index: number, playerStat: PlayerStat): JSX.Element {
        return <tr key={"player_stats_row" + index}>
            <td>{index + 1}</td>
            <td>{playerStat.firstName + ' ' + playerStat.lastName}</td>
            <td><TeamLink id={playerStat.teamId} name={playerStat.teamName} /></td>
            <td><LeagueUnitLink id={playerStat.leagueUnitId} name={playerStat.leagueUnitName} /></td>
            <td className="value">{ageFormatter(playerStat.age)}</td>
            <td className="value">{playerStat.games}</td>
            <td className="value">{playerStat.played}</td>
            <td className="value">{playerStat.scored}</td>
            <td className="value">{yellowCards(playerStat.yellowCards)}</td>
            <td className="value">{redCards(playerStat.redCards)}</td>
            <td className="value">{playerStat.totalInjuries}</td>
            <td className="value">{Math.floor(playerStat.goalRate)}</td>
        </tr>
    }
}

export default PlayerStats