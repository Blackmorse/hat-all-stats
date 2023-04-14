import React from 'react'
import { useTranslation } from 'react-i18next'
import { getTeamAgeInjuries } from '../../../rest/Client'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import TeamAgeInjury from '../../../rest/models/team/TeamAgeInjury'
import { PagesEnum } from '../../enums/PagesEnum'
import { injuryFormatter } from '../../Formatters'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import HookAbstractTableSection from '../HookAbstractTableSection'
import { SelectorsEnum } from '../SelectorsEnum'
import TableColumns from '../TableColumns'

const TeamAgeInjuryTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()

    return <HookAbstractTableSection<LevelProps, TeamAgeInjury>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getTeamAgeInjuries(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='age'
        defaultStatsType={{statType: StatsTypeEnum.ROUND, roundNumber: props.levelDataProps.currentRound()}}
        pageEnum={PagesEnum.TEAM_AGE_INJURY}
        statsTypes={[StatsTypeEnum.ROUND]}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.teamTableColumn(tai => tai.teamSortingKey, props.showCountryFlags),
            TableColumns.leagueUnitTableColumn(tai => tai.teamSortingKey),
            TableColumns.ageTableColumn(tai => tai.age, 'age'),
            {
                columnHeader: { title: t('table.total_injury_weeks'), sortingField: 'injury', center: true },
                columnValue:  { provider: tai => injuryFormatter(tai.injury), center: true }
            },
            TableColumns.simpleNumber(tai => tai.injuryCount, t('table.total_injury_number'), 'injury_count')
        ]}
    />
}

export default TeamAgeInjuryTable
