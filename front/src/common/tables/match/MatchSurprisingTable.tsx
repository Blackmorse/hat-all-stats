import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import React from 'react';
import MatchTopHatstats from '../../../rest/models/match/MatchTopHatstats';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import '../../../i18n'
import { useTranslation } from 'react-i18next'
import { getSurprisingMatches } from '../../../rest/Client';
import HookAbstractTableSection from '../HookAbstractTableSection';
import { SelectorsEnum } from '../SelectorsEnum';
import TableColumns from '../TableColumns';
import TeamMatchInfoExecutableSection from '../../../team/matches/TeamMatchInfoExecutableSection';
import { PagesEnum } from '../../enums/PagesEnum';

const MatchSurprisingTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {
    const [ t, _i18n ] = useTranslation()
   
    return <HookAbstractTableSection<LevelProps, MatchTopHatstats>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getSurprisingMatches(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='abs_hatstats_difference'
        defaultStatsType={{statType: StatsTypeEnum.ACCUMULATE}}
        statsTypes={[StatsTypeEnum.ACCUMULATE, StatsTypeEnum.ROUND]}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        pageEnum={PagesEnum.MATCH_SURPRISING}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.leagueUnitTableColumn<MatchTopHatstats>(mth => mth.homeTeam),
            TableColumns.teamTableColumn(mth => mth.homeTeam, props.showCountryFlags),
            TableColumns.loddarStatsTableColumn(mth => mth.homeLoddarStats, 'abs_loddar_stats_difference'),
            TableColumns.hatstatsTableColumn(mth => mth.homeHatstats, 'abs_hatstats_difference'),
            {
                columnHeader: {title: t('overview.goals'), sortingField: 'abs_goals_difference', center: true},
                columnValue: {provider: (mth) => mth.homeGoals + ' : ' + mth.awayGoals, center: true}
            },
            TableColumns.hatstatsTableColumn(mth => mth.awayHatstats, 'abs_hatstats_difference'),
            TableColumns.loddarStatsTableColumn(mth => mth.awayLoddarStats, 'abs_loddar_stats_difference'),
            TableColumns.teamTableColumn(mth => mth.awayTeam, props.showCountryFlags)
        ]}
        expandedRowFunc={mth => <TeamMatchInfoExecutableSection matchId={mth.matchId}/>}
    />
}

export default MatchSurprisingTable
