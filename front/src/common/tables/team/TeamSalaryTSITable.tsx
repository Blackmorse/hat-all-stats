import React from 'react';
import AbstractTableSection, { SortingState, DataRequest } from '../AbstractTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TeamSalaryTSI from '../../../rest/models/team/TeamSalaryTSI';
import { getTeamSalaryTSI } from '../../../rest/Client';
import '../../../i18n'
import { Translation } from 'react-i18next'
import ModelTableTh from '../../../common/elements/SortingTableTh'
import LeagueUnitLink from '../../links/LeagueUnitLink';
import TeamLink from '../../links/TeamLink'
import { commasSeparated, salaryFormatter, doubleSalaryFormatter } from '../../Formatters'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import { LoadingEnum } from '../../enums/LoadingEnum'
import RestTableData from '../../../rest/models/RestTableData'
import { SelectorsEnum } from '../SelectorsEnum';
import HattidTooltip from '../../elements/HattidTooltip';

class TeamSalaryTSITable<TableProps extends LevelDataProps>
    extends AbstractTableSection<TableProps, TeamSalaryTSI, RestTableData<TeamSalaryTSI>> {
    
    constructor(props: LevelDataPropsWrapper<TableProps>) {
        super(props, 'salary', {statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()},
            [StatsTypeEnum.ROUND],
            [SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR,
                SelectorsEnum.PLAYED_IN_LAST_MATCH_SELECTOR])
    }

    responseModelToRowModel(responseModel: RestTableData<TeamSalaryTSI>): RestTableData<TeamSalaryTSI> {
        return responseModel
    }

    executeDataRequest(dataRequest: DataRequest, callback: (loadingState: LoadingEnum, result?: RestTableData<TeamSalaryTSI>) => void): void {
        getTeamSalaryTSI(this.props.levelDataProps.createLevelRequest(), dataRequest.statisticsParameters, dataRequest.playedInLastMatch, 
            callback)
    }

    columnHeaders(sortingState: SortingState): JSX.Element {
        return <Translation>
            {
            t => 
            <tr>
                <HattidTooltip 
                    poppedHint={t('table.position')}
                    content={<th>{t('table.position_abbr')}</th>}
                />
                <th>{t('table.team')}</th>
                <th className="text-center">{t('table.league')}</th>
                <ModelTableTh title='table.tsi' sorting={{field: 'tsi', state: sortingState}} />
                <ModelTableTh title='table.salary' sorting={{field: 'salary', 
                    state: sortingState}} titlePostfix={', ' + this.props.levelDataProps.currency()}/>
                <ModelTableTh title='menu.players' sorting={{field: 'players_count', state: sortingState}}/>
                <ModelTableTh title='table.average_tsi' sorting={{field: 'avg_tsi', state: sortingState}} />
                <ModelTableTh title='table.average_salary' titlePostfix={', ' + this.props.levelDataProps.currency()} sorting={{field: 'avg_salary', state: sortingState}} />
                <ModelTableTh title='table.salary_per_tsi' sorting={{field: 'salary_per_tsi', state: sortingState}} titlePostfix={', ' + this.props.levelDataProps.currency()}/>
            </tr>
        }
        </Translation>
    }

    row(index: number, className: string, teamSalaryTSI: TeamSalaryTSI): JSX.Element {
        let teamSortingKey = teamSalaryTSI.teamSortingKey
        return <tr className={className}>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} text={teamSortingKey.teamName} /></td>
            <td className="text-center"><LeagueUnitLink id={teamSortingKey.leagueUnitId} text={teamSortingKey.leagueUnitName}/></td>
            <td className="text-center">{commasSeparated(teamSalaryTSI.tsi)}</td>
            <td className="text-center">{salaryFormatter(teamSalaryTSI.salary, this.props.levelDataProps.currencyRate())}</td>
            <td className="text-center">{teamSalaryTSI.playersCount}</td>
            <td className="text-center">{commasSeparated(teamSalaryTSI.avgTsi)}</td>
            <td className="text-center">{salaryFormatter(teamSalaryTSI.avgSalary, this.props.levelDataProps.currencyRate())}</td>
            <td className="text-center">{doubleSalaryFormatter(teamSalaryTSI.salaryPerTsi, this.props.levelDataProps.currencyRate())}</td>
        </tr>
    }
}

export default TeamSalaryTSITable
