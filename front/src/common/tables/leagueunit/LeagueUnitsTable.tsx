import React from 'react'
import LeagueUnitRating from '../../../rest/models/leagueunit/LeagueUnitRating'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TableSection from '../TableSection'
import { getLeagueUnits } from '../../../rest/Client'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import { SelectorsEnum } from '../SelectorsEnum'
import TableColumns from '../TableColumns'
import { useTranslation } from 'react-i18next'
import { loddarStats } from '../../Formatters'
import { PagesEnum } from '../../enums/PagesEnum'


const LeagueUnitsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <TableSection<LevelProps, LeagueUnitRating>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getLeagueUnits(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='hatstats'
        statsTypes={[StatsTypeEnum.AVG, StatsTypeEnum.MAX, StatsTypeEnum.ROUND]}
        defaultStatsType={{statType: StatsTypeEnum.MAX}}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
            SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        pageEnum={PagesEnum.LEAGUE_UNITS}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.leagueUnitTableColumn(lur => lur),
            {
                columnHeader: { title: t('table.hatstats'), sortingField: 'hatstats' },
                columnValue:  { provider: lur => lur.hatStats.toString(), center: true }
            },
            {
                columnHeader: { title: t('table.midfield'), sortingField: 'midfield' },
                columnValue:  { provider: lur => (lur.midfield * 3).toString(), center: true }
            },
            {
                columnHeader: { title: t('table.defense'), sortingField: 'defense' },
                columnValue:  { provider: lur => lur.defense.toString(), center: true }
            },
            {
                columnHeader: { title: t('table.attack'), sortingField: 'attack' },
                columnValue:  { provider: lur => lur.attack.toString(), center: true }
            },
            {
                columnHeader: { title: t('table.loddar_stats'), sortingField: 'loddar_stats' },
                columnValue:  { provider: lur => loddarStats(lur.loddarStats), center: true }
            },
        ]}
    />
}

export default LeagueUnitsTable
