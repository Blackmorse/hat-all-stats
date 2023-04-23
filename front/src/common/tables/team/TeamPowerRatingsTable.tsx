import React from 'react'
import { useTranslation } from 'react-i18next'
import { getTeamPowerRatings } from '../../../rest/Client'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import TeamPowerRating from '../../../rest/models/team/TeamPowerRating'
import { PagesEnum } from '../../enums/PagesEnum'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TableSection from '../TableSection'
import { SelectorsEnum } from '../SelectorsEnum'
import TableColumns from '../TableColumns'

const TeamPowerRatingsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <TableSection<LevelProps, TeamPowerRating>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getTeamPowerRatings(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='power_rating'  
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        pageEnum={PagesEnum.TEAM_POWER_RATINGS}
        statsTypes={[StatsTypeEnum.ROUND]}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.teamTableColumn(tpr => tpr.teamSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(tpr => tpr.teamSortingKey),
            {
                columnHeader: { title: t('table.power_rating'), sortingField: 'power_rating', center: true },
                columnValue:  { provider: tpr => tpr.powerRating.toString(), center: true }
            }
        ]}
    />
}

export default TeamPowerRatingsTable
