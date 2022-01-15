import React from 'react';
import LeagueData from '../rest/models/leveldata/LeagueData';
import { PagesEnum } from '../common/enums/PagesEnum'
import LeagueLevelDataProps from './LeagueLevelDataProps'
import LeagueOverviewPage from './LeagueOverviewPage';
import TeamHatstatsTable from '../common/tables/team/TeamHatstatsTable';
import LeagueUnitsTable from '../common/tables/leagueunit/LeagueUnitsTable';
import PlayerGoalGamesTable from '../common/tables/player/PlayerGoalsGamesTable'
import PlayerCardsTable from '../common/tables/player/PlayerCardsTable';
import PlayerSalaryTsiTable from '../common/tables/player/PlayerSalaryTsiTable';
import PlayerRatingsTable from '../common/tables/player/PlayerRatingsTable';
import PlayerInjuriesTable from '../common/tables/player/PlayerInjuriesTable';
import TeamSalaryTSITable from '../common/tables/team/TeamSalaryTSITable';
import TeamCardsTable from '../common/tables/team/TeamCardsTable';
import TeamRatingsTable from '../common/tables/team/TeamRatingsTable';
import TeamAgeInjuryTable from '../common/tables/team/TeamAgeInjuryTable';
import TeamGoalPointsTable from '../common/tables/team/TeamGoalPointsTable';
import TeamPowerRatingsTable from '../common/tables/team/TeamPowerRatingsTable';
import TeamFanclubFlagsTable from '../common/tables/team/TeamFanclubFlagsTable';
import TeamStreakTrophiesTable from '../common/tables/team/TeamStreakTrophiesTable';
import MatchTopHatstatsTable from '../common/tables/match/MatchTopHatstatsTable';
import MatchSurprisingTable from '../common/tables/match/MatchSurprisingTable';
import MatchSpectatorsTable from '../common/tables/match/MatchSpectatorsTable';
import OldestTeamsTable from '../common/tables/team/OldestTeamsTable'
import PromotionsTable from '../common/pages/PromotionsTable'
import QueryParams from '../common/QueryParams';
import DreamTeamPage from '../common/pages/DreamTeamPage';
import TeamSearchPage from '../common/pages/TeamSearchPage';

export default function pages(): Map<PagesEnum, (props: LeagueLevelDataProps, queryParams: QueryParams) => JSX.Element> {
    const pagesMap = new Map<PagesEnum, (props: LeagueLevelDataProps, queryParams: QueryParams) => JSX.Element>()
    pagesMap.set(PagesEnum.OVERVIEW, 
        (props, _queryParams) => <LeagueOverviewPage levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_HATSTATS, 
        (props, queryParams) => <TeamHatstatsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.LEAGUE_UNITS,
        (props, queryParams) => <LeagueUnitsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.DREAM_TEAM,
        (props, queryParams) => <DreamTeamPage<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams}/>)
    pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES,
        (props, queryParams) => <PlayerGoalGamesTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams}/>)
    pagesMap.set(PagesEnum.PLAYER_CARDS,
        (props, queryParams) => <PlayerCardsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.PLAYER_SALARY_TSI,
        (props, queryParams) => <PlayerSalaryTsiTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.PLAYER_RATINGS, 
        (props, queryParams) => <PlayerRatingsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.PLAYER_INJURIES,
        (props, queryParams) => <PlayerInjuriesTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_SALARY_TSI,
        (props, queryParams) => <TeamSalaryTSITable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_CARDS,
        (props, queryParams) => <TeamCardsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams}  />)
    pagesMap.set(PagesEnum.TEAM_RATINGS,
        (props, queryParams) => <TeamRatingsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_AGE_INJURY,
        (props, queryParams) => <TeamAgeInjuryTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_GOAL_POINTS,
        (props, queryParams) => <TeamGoalPointsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_POWER_RATINGS,
        (props, queryParams) => <TeamPowerRatingsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS,
        (props, queryParams) => <TeamFanclubFlagsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES,
        (props, queryParams) => <TeamStreakTrophiesTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.OLDEST_TEAMS,
        (props, queryParams) => <OldestTeamsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS,
        (props, queryParams) => <MatchTopHatstatsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.MATCH_SURPRISING,
        (props, queryParams) => <MatchSurprisingTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.MATCH_SPECTATORS,
        (props, queryParams) => <MatchSpectatorsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.PROMOTIONS,
        (props, queryParams) => <PromotionsTable<LeagueData, LeagueLevelDataProps> levelDataProps={props} queryParams={queryParams} />)

    pagesMap.set(PagesEnum.TEAM_SEARCH, (_props, _queryParams) => <TeamSearchPage />)
    return pagesMap
}
