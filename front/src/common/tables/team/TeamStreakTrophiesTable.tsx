import React from 'react'
import { useTranslation } from 'react-i18next'
import { getTeamStreakTrophies } from '../../../rest/clients/TeamStatsClient'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import TeamStreakTrophies from '../../../rest/models/team/TeamStreakTrophies'
import { PagesEnum } from '../../enums/PagesEnum'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import TableSection from '../TableSection'
import { SelectorsEnum } from '../SelectorsEnum'
import TableColumns from '../TableColumns'

const TeamStreakTrophiesTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <TableSection<LevelProps, TeamStreakTrophies>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getTeamStreakTrophies(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='trophies_number'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        pageEnum={PagesEnum.TEAM_STREAK_TROPHIES}
        statsTypes={[StatsTypeEnum.ROUND]}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.teamTableColumn(tst => tst.teamSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(tst => tst.teamSortingKey),
            TableColumns.simpleNumber(tst => tst.trophiesNumber, { title: t('table.trophies') }, 'trophies_number'),
            TableColumns.simpleNumber(tst => tst.numberOfVictories, { title: t('table.victories') }, 'number_of_victories'),
            TableColumns.simpleNumber(tst => tst.numberOfUndefeated, { title: t('table.undefeated') }, 'number_of_undefeated')
        ]}
    />
}

export default TeamStreakTrophiesTable
