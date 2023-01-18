import React from 'react';
import { useTranslation } from 'react-i18next';
import { getTeamSalaryTSI } from '../../../rest/clients/TeamStatsClient';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import TeamSalaryTSI from '../../../rest/models/team/TeamSalaryTSI';
import { PagesEnum } from '../../enums/PagesEnum';
import { doubleSalaryFormatter } from '../../Formatters';
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps';
import HookAbstractTableSection from '../HookAbstractTableSection';
import { SelectorsEnum } from '../SelectorsEnum';
import TableColumns from '../TableColumns';

const TeamSalaryTSITable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <HookAbstractTableSection<LevelProps, TeamSalaryTSI>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => 
            getTeamSalaryTSI(props.levelDataProps.createLevelRequest(), request.statisticsParameters, 
                request.playedInLastMatch, request.excludeZeroTsi, callback)}
        defaultSortingField='sum_salary'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        selectors={[SelectorsEnum.SEASON_SELECTOR, 
            SelectorsEnum.STATS_TYPE_SELECTOR, 
            SelectorsEnum.PAGE_SIZE_SELECTOR, 
            SelectorsEnum.PAGE_SELECTOR,
            SelectorsEnum.PLAYED_IN_LAST_MATCH_SELECTOR,
            SelectorsEnum.EXCLUDE_ZERO_TSI_PLAYERS]}
        statsTypes={[StatsTypeEnum.ROUND]}
        pageEnum={PagesEnum.TEAM_SALARY_TSI}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.teamTableColumn(pst => pst.teamSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(pst => pst.teamSortingKey),
            TableColumns.tsi(tst => tst.tsi, t('table.tsi'), 'tsi'),
            TableColumns.salary(tst => tst.salary, props.levelDataProps.currencyRate(), t('table.salary') + ', ' + props.levelDataProps.currency(), 'sum_salary'),
            {
                columnHeader: { title: t('menu.players'), sortingField: 'players_count' },
                columnValue: { provider: (tst) => tst.playersCount.toString(), center: true }
            },
            TableColumns.tsi(tst => tst.avgTsi, t('table.average_tsi'), 'avg_tsi'),
            TableColumns.salary(tst => tst.avgSalary, props.levelDataProps.currencyRate(), t('table.average_salary') + ', ' + props.levelDataProps.currency(), 'avg_salary'),
            {
                columnHeader: { title: t('table.salary_per_tsi') + ', ' + props.levelDataProps.currency(), sortingField: 'salary_per_tsi' },
                columnValue: { provider: (tst) => doubleSalaryFormatter(tst.salaryPerTsi, props.levelDataProps.currencyRate()).toString(), center: true }
            }
        ]}
    />
}

export default TeamSalaryTSITable
