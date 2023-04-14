import React from 'react'
import { useTranslation } from 'react-i18next'
import { getMatchSpectators } from '../../../rest/Client'
import MatchSpectators from '../../../rest/models/match/MatchSpectators'
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters'
import { PagesEnum } from '../../enums/PagesEnum'
import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import HookAbstractTableSection from '../HookAbstractTableSection'
import { SelectorsEnum } from '../SelectorsEnum'
import TableColumns from '../TableColumns'

const MatchSpectatorsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()
    
    return <HookAbstractTableSection<LevelProps, MatchSpectators>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getMatchSpectators(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='sold_total'
        defaultStatsType={{statType: StatsTypeEnum.ACCUMULATE}}
        statsTypes={[StatsTypeEnum.ACCUMULATE, StatsTypeEnum.ROUND]}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        pageEnum={PagesEnum.MATCH_SPECTATORS}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.leagueUnitTableColumn(ms => ms.homeTeam),
            TableColumns.teamTableColumn(ms => ms.homeTeam, props.showCountryFlags),
            {
                columnHeader: {title: '', center: true},
                columnValue: {provider: (ms) => ms.homeGoals + ' : ' + ms.awayGoals, center: true}
            },
            TableColumns.teamTableColumn(ms => ms.awayTeam, props.showCountryFlags),
            {
                columnHeader: { title: t('matches.spectatos'), sortingField: 'sold_total', center: true },
                columnValue: { provider: ms => ms.spectators.toString(), center: true }
            }
        ]}
    />
}

export default MatchSpectatorsTable
