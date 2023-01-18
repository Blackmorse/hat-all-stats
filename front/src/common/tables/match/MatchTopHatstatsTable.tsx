import LevelDataProps, { LevelDataPropsWrapper } from '../../LevelDataProps'
import React from 'react';
import MatchTopHatstats from '../../../rest/models/match/MatchTopHatstats';
import { StatsTypeEnum } from '../../../rest/models/StatisticsParameters';
import '../../../i18n'
import { getMatchesTopHatstats } from '../../../rest/Client';
import HookAbstractTableSection from '../HookAbstractTableSection';
import {SelectorsEnum} from '../SelectorsEnum';
import TableColumns from '../TableColumns';
import TeamMatchInfoExecutableSection from '../../../team/matches/TeamMatchInfoExecutableSection';
import { PagesEnum } from '../../enums/PagesEnum';

const MatchTopHatstatsTable = <LevelProps extends LevelDataProps>(props: LevelDataPropsWrapper<LevelProps>) => {

    return <HookAbstractTableSection<LevelProps, MatchTopHatstats>
        levelProps={props.levelDataProps}
        requestFunc={(request, callback) => getMatchesTopHatstats(props.levelDataProps.createLevelRequest(), request.statisticsParameters, callback)}
        defaultSortingField='sum_hatstats'
        defaultStatsType={{statType: StatsTypeEnum.ACCUMULATE}}
        statsTypes={[StatsTypeEnum.ACCUMULATE, StatsTypeEnum.ROUND]}
        selectors={[SelectorsEnum.SEASON_SELECTOR, SelectorsEnum.STATS_TYPE_SELECTOR, 
                SelectorsEnum.PAGE_SIZE_SELECTOR, SelectorsEnum.PAGE_SELECTOR]}
        pageEnum={PagesEnum.MATCH_TOP_HATSTATS}
        tableColumns={[
            TableColumns.postitionsTableColumn(),
            TableColumns.leagueUnitTableColumn(mth => mth.homeTeam),
            TableColumns.teamTableColumn(mth => mth.homeTeam, props.showCountryFlags),
            TableColumns.loddarStatsTableColumn(mth => mth.homeLoddarStats, 'sum_loddar_stats'),
            TableColumns.hatstatsTableColumn(mth => mth.homeHatstats, 'sum_hatstats'),
            {
                columnHeader: {title: '', center: true},
                columnValue: {provider: (mth) => mth.homeGoals + ' : ' + mth.awayGoals, center: true}
            },
            TableColumns.hatstatsTableColumn(mth => mth.awayHatstats, 'sum_hatstats'),
            TableColumns.loddarStatsTableColumn(mth => mth.awayLoddarStats, 'sum_loddar_stats'),
            TableColumns.teamTableColumn(mth => mth.awayTeam, props.showCountryFlags)
        ]}
        expandedRowFunc={mth => <TeamMatchInfoExecutableSection matchId={mth.matchId}/>}
    />
}

export default MatchTopHatstatsTable
