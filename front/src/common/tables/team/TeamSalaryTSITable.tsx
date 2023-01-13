import React from 'react';
import { useTranslation } from 'react-i18next';
import { getTeamSalaryTSI } from '../../../rest/clients/TeamStatsClient';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import TeamSalaryTSI from '../../../rest/models/team/TeamSalaryTSI';
import { commasSeparated, doubleSalaryFormatter, salaryFormatter } from '../../Formatters';
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps';
import HookAbstractTableSection from '../HookAbstractTableSection';
import { SelectorsEnum } from '../SelectorsEnum';
import TableColumns from '../TableColumns';

const TeamSalaryTSITable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <HookAbstractTableSection<LevelProps, TeamSalaryTSI>
        levelProps={props.levelDataProps}
        queryParams={props.queryParams}
        requestFunc={(request, callback) => 
            getTeamSalaryTSI(props.levelDataProps.createLevelRequest(), request.statisticsParameters, 
                request.playedInLastMatch, request.excludeZeroTsi, callback)}
        defaultSortingField='salary'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        selectors={[SelectorsEnum.SEASON_SELECTOR, 
            SelectorsEnum.STATS_TYPE_SELECTOR, 
            SelectorsEnum.PAGE_SIZE_SELECTOR, 
            SelectorsEnum.PAGE_SELECTOR,
            SelectorsEnum.PLAYED_IN_LAST_MATCH_SELECTOR,
            SelectorsEnum.EXCLUDE_ZERO_TSI_PLAYERS]}
        statsTypes={[StatsTypeEnum.ROUND]}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.teamTableColumn(pst => pst.teamSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(pst => pst.teamSortingKey),
            { 
                columnHeader: { title: t('table.tsi'), sortingField: 'tsi' },
                columnValue: { provider: (tst) => commasSeparated(tst.tsi), center: true }
            },
            {
                columnHeader: { title: t('table.salary') + ', ' + props.levelDataProps.currency(), sortingField: 'salary' },
                columnValue: { provider: (tst) => salaryFormatter(tst.salary, props.levelDataProps.currencyRate()), center: true }
            },
            {
                columnHeader: { title: t('menu.players'), sortingField: 'players_count' },
                columnValue: { provider: (tst) => tst.playersCount.toString() }
            },
            {
                columnHeader: { title: t('table.average_tsi'), sortingField: 'avg_tsi' },
                columnValue: { provider: (tst) =>  commasSeparated(tst.avgTsi), center: true}
            },
            {
                columnHeader: { title: t('table.average_salary') + ', ' + props.levelDataProps.currency(), sortingField: 'avg_salary' },
                columnValue: { provider: (tst) => salaryFormatter(tst.avgSalary, props.levelDataProps.currencyRate()), center: true }
            },
            {
                columnHeader: { title: t('table.salary_per_tsi') + ', ' + props.levelDataProps.currency(), sortingField: 'salary_per_tsi' },
                columnValue: { provider: (tst) => doubleSalaryFormatter(tst.salaryPerTsi, props.levelDataProps.currencyRate()).toString(), center: true }
            }
        ]}
    />
}

export default TeamSalaryTSITable