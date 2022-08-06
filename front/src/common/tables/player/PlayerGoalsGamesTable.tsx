import React from 'react';
import { SortingState } from '../AbstractTableSection'
import PlayersTableSection from '../PlayersTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import PlayerGoalsGames from '../../../rest/models/player/PlayerGoalsGames'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../../common/elements/SortingTableTh'
import TeamLink from '../../links/TeamLink'
import LeagueUnitLink from '../../links/LeagueUnitLink'
import { getPlayerGoalsGames } from '../../../rest/Client';
import ExternalPlayerLink from '../../links/ExternalPlayerLink';
import LeagueLink from '../../links/LeagueLink';
import CountryImage from '../../elements/CountryImage';
import Mappings from '../../enums/Mappings';
import i18n from '../../../i18n';
import { ageFormatter } from '../../Formatters'
import HattidTooltip from '../../elements/HattidTooltip';

abstract class PlayerGoalsGamesTable<TableProps extends LevelDataProps> 
    extends PlayersTableSection<TableProps, PlayerGoalsGames> {

    constructor(props: LevelDataPropsWrapper<TableProps>) {
        super(props, 'scored', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}, 
            [StatsTypeEnum.ROUND])
    }
        
    fetchDataFunction = getPlayerGoalsGames

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            t =>
            <tr>
                <HattidTooltip 
                    poppedHint={t('table.position')}
                    content={<th>{t('table.position_abbr')}</th>}
                />
                <th></th>
                <th>{t('table.player')}</th>
                <th>{t('table.team')}</th>
                <th className='text-center'>{t('table.league')}</th>
                <th></th>
                <th className='text-center'>{t('table.age')}</th>
                <ModelTableTh title='table.games' sortingField='games' sortingState={sortingState} />
                <ModelTableTh title='table.minutes' sortingField='played' sortingState={sortingState} />
                <ModelTableTh title='table.scored' sortingField='scored' sortingState={sortingState} />
                <ModelTableTh title='table.minutes_per_goal_abbr' poppedHint={t('table.minutes_per_goal')} sortingField='goal_rate' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    row(index: number, className: string, playerGoalsGames: PlayerGoalsGames): JSX.Element {
        let playerSortingKey = playerGoalsGames.playerSortingKey
        return <tr className={className}>
            <td>{index + 1}</td>
            <td className="text-center"><LeagueLink forceRefresh={true} id={playerSortingKey.nationality} text={<CountryImage countryId={playerSortingKey.nationality} text={this.props.levelDataProps.countriesMap().get(playerSortingKey.nationality)}/>} /></td>
            <td>{playerSortingKey.firstName + ' ' + playerSortingKey.lastName} <ExternalPlayerLink id={playerSortingKey.playerId}/></td>
            <td><TeamLink id={playerSortingKey.teamId} text={playerSortingKey.teamName} /></td>
            <td className="text-center"><LeagueUnitLink id={playerSortingKey.leagueUnitId} text={playerSortingKey.leagueUnitName} /></td>
            <td className="text-center">{i18n.t(Mappings.roleToTranslationMap.get(playerGoalsGames.role) || '')}</td>
            <td className="text-center">{ageFormatter(playerGoalsGames.age)}</td>
            <td className="text-center">{playerGoalsGames.games}</td>
            <td className="text-center">{playerGoalsGames.playedMinutes}</td>
            <td className="text-center">{playerGoalsGames.scored}</td>
            <td className="text-center">{Math.floor(playerGoalsGames.goalRate)}</td>
        </tr>
    }
}

export default PlayerGoalsGamesTable
