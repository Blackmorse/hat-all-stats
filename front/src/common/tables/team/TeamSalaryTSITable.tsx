import React from 'react';
import AbstractTableSection, { SortingState, DataRequest } from '../AbstractTableSection'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import LevelData from '../../../rest/models/leveldata/LevelData';
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

class TeamSalaryTSITable<Data extends LevelData, TableProps extends LevelDataProps<Data>>
    extends AbstractTableSection<Data, TableProps, TeamSalaryTSI, RestTableData<TeamSalaryTSI>> {
    
    constructor(props: LevelDataPropsWrapper<Data, TableProps>) {
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
            (t, { i18n }) =>
            <tr>
                <HattidTooltip 
                    poppedHint={t('table.position')}
                    content={<th>{t('table.position_abbr')}</th>}
                />
                <th>{t('table.team')}</th>
                <th className="text-center">{t('table.league')}</th>
                <ModelTableTh title='table.tsi' sortingField='tsi' sortingState={sortingState} />
                <ModelTableTh title='table.salary' sortingField='salary' titlePostfix={', ' + this.props.levelDataProps.currency()}
                     sortingState={sortingState} />
                <ModelTableTh title='menu.players' sortingField='players_count' sortingState={sortingState}/>
                <ModelTableTh title='table.average_tsi' sortingField='avg_tsi' sortingState={sortingState} />
                <ModelTableTh title='table.average_salary' titlePostfix={', ' + this.props.levelDataProps.currency()} 
                    sortingField='avg_salary' sortingState={sortingState} />
                <ModelTableTh title='table.salary_per_tsi' sortingField='salary_per_tsi' titlePostfix={', ' + this.props.levelDataProps.currency()}
                    sortingState={sortingState} />
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
