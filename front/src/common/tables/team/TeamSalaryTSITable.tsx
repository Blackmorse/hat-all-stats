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
import { commasSeparated, salaryFormatter } from '../../Formatters'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import { LoadingEnum } from '../../enums/LoadingEnum'
import RestTableData from '../../../rest/models/RestTableData'
import { SelectorsEnum } from '../SelectorsEnum';

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
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th>{t('table.team')}</th>
                <th className="value">{t('table.league')}</th>
                <ModelTableTh title='table.tsi' sortingField='tsi' sortingState={sortingState} />
                <ModelTableTh title='table.salary' sortingField='salary' titlePostfix={', ' + this.props.levelDataProps.currency()}
                     sortingState={sortingState} />
                <ModelTableTh title='menu.players' sortingField='players_count' sortingState={sortingState}/>
                <ModelTableTh title='table.average_tsi' sortingField='avg_tsi' sortingState={sortingState} />
                <ModelTableTh title='table.average_salary' titlePostfix={', ' + this.props.levelDataProps.currency()} 
                    sortingField='avg_salary' sortingState={sortingState} />
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, teamSalaryTSI: TeamSalaryTSI): JSX.Element {
        let teamSortingKey = teamSalaryTSI.teamSortingKey
        return <>
            <td>{index + 1}</td>
            <td><TeamLink id={teamSortingKey.teamId} text={teamSortingKey.teamName} /></td>
            <td className="value"><LeagueUnitLink id={teamSortingKey.leagueUnitId} text={teamSortingKey.leagueUnitName}/></td>
            <td className="value">{commasSeparated(teamSalaryTSI.tsi)}</td>
            <td className="value">{salaryFormatter(teamSalaryTSI.salary, this.props.levelDataProps.currencyRate())}</td>
            <td className="value">{teamSalaryTSI.playersCount}</td>
            <td className="value">{commasSeparated(teamSalaryTSI.avgTsi)}</td>
            <td className="value">{salaryFormatter(teamSalaryTSI.avgSalary, this.props.levelDataProps.currencyRate())}</td>
        </>
    }
}

export default TeamSalaryTSITable