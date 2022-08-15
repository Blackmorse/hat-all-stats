import React from 'react';
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
import Mappings from '../../enums/Mappings';
import i18n from '../../../i18n';
import HattidTooltip from '../../elements/HattidTooltip';
import PlayerLink from '../../links/PlayerLink';

abstract class PlayerSalaryTsiTable<TableProps extends LevelDataProps> 
    extends PlayersTableSection<TableProps, PlayerSalaryTSI> {

    constructor(props: LevelDataPropsWrapper<TableProps>) {
        super(props, 'tsi', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND])
    }

    fetchDataFunction = getPlayerSalaryTsi

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            t =>
            <tr>
                <HattidTooltip 
                    poppedHint={t('table.position')}
                    content={<th>{t('table.position_abbr')}</th>}
                />
                <th>{t('table.player')}</th>
                <th>{t('table.team')}</th>
                <th className="value">{t('table.league')}</th>
                <th></th>
                <ModelTableTh title='table.age' sorting={{field: 'age', state: sortingState}} />
                <ModelTableTh title='table.tsi' sorting={{field: 'tsi', state: sortingState}} />
                <ModelTableTh title='table.salary' sorting={{field: 'salary', state: sortingState}} titlePostfix={', ' + this.props.levelDataProps.currency()} />
            </tr>
        }
        </Translation>
    }

    row(index: number, className: string, playerSalaryTSI: PlayerSalaryTSI): JSX.Element {
        let playerSortingKey = playerSalaryTSI.playerSortingKey
        return <tr className={className}>
            <td>{index + 1}</td>
            <td>
                <PlayerLink
                    id={playerSortingKey.playerId}
                    text={playerSortingKey.firstName + ' ' + playerSortingKey.lastName}
                    nationality={playerSortingKey.nationality}
                    countriesMap={this.props.levelDataProps.countriesMap()}
                    externalLink
                />
            </td>
            <td>{playerSortingKey.firstName + ' ' + playerSortingKey.lastName} <ExternalPlayerLink id={playerSortingKey.playerId}/></td>
            <td><TeamLink id={playerSortingKey.teamId} text={playerSortingKey.teamName} 
               flagCountryNumber={this.props.showCountryFlags !== undefined && this.props.showCountryFlags ? playerSortingKey.teamLeagueId : undefined} /></td>
            <td className="text-center"><LeagueUnitLink id={playerSortingKey.leagueUnitId} text={playerSortingKey.leagueUnitName} /></td>
            <td className="text-center">{i18n.t(Mappings.roleToTranslationMap.get(playerSalaryTSI.role) || '')}</td>
            <td className="text-center">{ageFormatter(playerSalaryTSI.age)}</td>
            <td className="text-center">{commasSeparated(playerSalaryTSI.tsi)}</td>
            <td className="text-center">{salaryFormatter(playerSalaryTSI.salary, this.props.levelDataProps.currencyRate())}</td>
        </tr>
    }
}

export default PlayerSalaryTsiTable
