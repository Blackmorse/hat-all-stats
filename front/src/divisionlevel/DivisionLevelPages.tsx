import React from 'react';
import { PagesEnum } from '../common/enums/PagesEnum';
import DivisionLevelDataProps from './DivisionLevelDataProps'
import DivisionLevelOverviewPage from './DivisionLevelOverviewPage';
import TeamHatstatsTable from '../common/tables/team/TeamHatstatsTable';
import LeagueUnitsTable from '../common/tables/leagueunit/LeagueUnitsTable';
import PlayerGoalsGamesTable from '../common/tables/player/PlayerGoalsGamesTable';
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
import QueryParams from '../common/QueryParams';
import PromotionsTable from '../common/pages/PromotionsTable';
import DreamTeamPage from '../common/pages/DreamTeamPage';
import OldestTeamsTable from '../common/tables/team/OldestTeamsTable';
import TeamSearchPage from '../common/pages/TeamSearchPage';

export default function pages(): Map<PagesEnum, (props: DivisionLevelDataProps, queryParams: QueryParams) => JSX.Element> {

    const pagesMap = new Map<PagesEnum, (props: DivisionLevelDataProps, queryParams: QueryParams) => JSX.Element>()
    pagesMap.set(PagesEnum.OVERVIEW, 
        (props, _queryParams) => <DivisionLevelOverviewPage levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_HATSTATS,
        (props, queryParams) => <TeamHatstatsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams}/>)
    pagesMap.set(PagesEnum.LEAGUE_UNITS,
        (props, queryParams) => <LeagueUnitsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams}/>)
    pagesMap.set(PagesEnum.DREAM_TEAM,
        (props, queryParams) => <DreamTeamPage<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams}/>)
    pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES,
        (props, queryParams) => <PlayerGoalsGamesTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams}/>) 
    pagesMap.set(PagesEnum.PLAYER_CARDS,
        (props, queryParams) => <PlayerCardsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.PLAYER_SALARY_TSI,
        (props, queryParams) => <PlayerSalaryTsiTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.PLAYER_RATINGS,
        (props, queryParams) => <PlayerRatingsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.PLAYER_INJURIES,
        (props, queryParams) => <PlayerInjuriesTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_SALARY_TSI,
        (props, queryParams) => <TeamSalaryTSITable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_CARDS,
        (props, queryParams) => <TeamCardsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_RATINGS,
        (props, queryParams) => <TeamRatingsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_AGE_INJURY,
        (props, queryParams) => <TeamAgeInjuryTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_GOAL_POINTS,
        (props, queryParams) => <TeamGoalPointsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_POWER_RATINGS,
        (props, queryParams) => <TeamPowerRatingsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS,
        (props, queryParams) => <TeamFanclubFlagsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES,
        (props, queryParams) => <TeamStreakTrophiesTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.OLDEST_TEAMS,
        (props, queryParams) => <OldestTeamsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS,
        (props, queryParams) => <MatchTopHatstatsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.MATCH_SURPRISING,
        (props, queryParams) => <MatchSurprisingTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.MATCH_SPECTATORS,
        (props, queryParams) => <MatchSpectatorsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)

    pagesMap.set(PagesEnum.PROMOTIONS,
        (props, queryParams) => <PromotionsTable<DivisionLevelDataProps> levelDataProps={props} queryParams={queryParams} />)

    pagesMap.set(PagesEnum.TEAM_SEARCH, (_props, _queryParams) => <TeamSearchPage />)
    return pagesMap
}
