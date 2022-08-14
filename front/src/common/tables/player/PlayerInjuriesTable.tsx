import React from 'react';
import { SortingState } from '../AbstractTableSection'
import ClassicTableSection from '../ClassicTableSection'
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
import HattidTooltip from '../../elements/HattidTooltip';

abstract class PlayerInjuriesTable<TableProps extends LevelDataProps>
        extends ClassicTableSection<TableProps, PlayerInjury>{
    constructor(props: LevelDataPropsWrapper<TableProps>) {
        super(props, 'injury', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getPlayerInjuries

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
                <th className="value">{t('table.league')}</th>
                <ModelTableTh title='table.age' sorting={{field: 'age', state: sortingState}} />
                <ModelTableTh title='table.injury' sorting={{field: 'injury', state: sortingState}} />
            </tr>
        }
        </Translation>
    }

    row(index: number, className: string, playerInjury: PlayerInjury): JSX.Element {
        let playerSortingKey = playerInjury.playerSortingKey
        return <tr className={className}>
            <td>{index + 1}</td>
            <td className="text-center"><LeagueLink forceRefresh={true} id={playerSortingKey.nationality} text={<CountryImage countryId={playerSortingKey.nationality} text={this.props.levelDataProps.countriesMap().get(playerSortingKey.nationality)}/>} /></td>
            <td>{playerSortingKey.firstName + ' ' + playerSortingKey.lastName} <ExternalPlayerLink id={playerSortingKey.playerId}/></td>
            <td><TeamLink id={playerSortingKey.teamId} text={playerSortingKey.teamName} /></td>
            <td className="text-center"><LeagueUnitLink id={playerSortingKey.leagueUnitId} text={playerSortingKey.leagueUnitName} /></td>
            <td className="text-center">{ageFormatter(playerInjury.age)}</td>
            <td className="text-center">{injuryFormatter(playerInjury.injury)}</td>
        </tr>
    }
}

export default PlayerInjuriesTable
