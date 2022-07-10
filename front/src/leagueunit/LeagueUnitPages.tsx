import React from 'react';
import {PagesEnum} from '../common/enums/PagesEnum';
import DreamTeamPage from '../common/pages/DreamTeamPage';
import PromotionsTable from '../common/pages/PromotionsTable';
import TeamSearchPage from '../common/pages/TeamSearchPage';
import QueryParams from '../common/QueryParams';
import MatchSpectatorsTable from '../common/tables/match/MatchSpectatorsTable';
import MatchSurprisingTable from '../common/tables/match/MatchSurprisingTable';
import MatchTopHatstatsTable from '../common/tables/match/MatchTopHatstatsTable';
import PlayerCardsTable from '../common/tables/player/PlayerCardsTable';
import PlayerGoalGamesTable from '../common/tables/player/PlayerGoalsGamesTable';
import PlayerInjuriesTable from '../common/tables/player/PlayerInjuriesTable';
import PlayerRatingsTable from '../common/tables/player/PlayerRatingsTable';
import PlayerSalaryTsiTable from '../common/tables/player/PlayerSalaryTsiTable';
import OldestTeamsTable from '../common/tables/team/OldestTeamsTable';
import TeamAgeInjuryTable from '../common/tables/team/TeamAgeInjuryTable';
import TeamCardsTable from '../common/tables/team/TeamCardsTable';
import TeamFanclubFlagsTable from '../common/tables/team/TeamFanclubFlagsTable';
import {TeamHatstatsTableSection} from '../common/tables/team/TeamHatstatsTable';
import TeamPowerRatingsTable from '../common/tables/team/TeamPowerRatingsTable';
import TeamRatingsTable from '../common/tables/team/TeamRatingsTable';
import TeamSalaryTSITable from '../common/tables/team/TeamSalaryTSITable';
import TeamStreakTrophiesTable from '../common/tables/team/TeamStreakTrophiesTable';
import {default as LeagueLevelDataProps, default as LeagueUnitLevelDataProps} from './LeagueUnitLevelDataProps';
import TeamPositionsTable from './TeamPositionsTable';

export default function pages(): Map<PagesEnum, (props: LeagueLevelDataProps, queryParams: QueryParams) => JSX.Element> {
    const pagesMap = new Map<PagesEnum, (props: LeagueUnitLevelDataProps, queryParams: QueryParams) => JSX.Element>()
    pagesMap.set(PagesEnum.TEAM_HATSTATS, 
        (props, queryParams) => <>
            <TeamPositionsTable levelDataProps={props} queryParams={queryParams} />
            <TeamHatstatsTableSection<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams}/>
            </>)
    pagesMap.set(PagesEnum.DREAM_TEAM,
        (props, queryParams) => <DreamTeamPage<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams}/>)
    pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES,
        (props, queryParams) => <PlayerGoalGamesTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams}/>)
    pagesMap.set(PagesEnum.PLAYER_CARDS,
        (props, queryParams) => <PlayerCardsTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.PLAYER_SALARY_TSI,
        (props, queryParams) => <PlayerSalaryTsiTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.PLAYER_RATINGS,
        (props, queryParams) => <PlayerRatingsTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.PLAYER_INJURIES,
        (props, queryParams) => <PlayerInjuriesTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_SALARY_TSI,
        (props, queryParams) => <TeamSalaryTSITable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_CARDS,
        (props, queryParams) => <TeamCardsTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)
    pagesMap.set(PagesEnum.TEAM_RATINGS,
        (props, queryParams) => <TeamRatingsTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)  
    pagesMap.set(PagesEnum.TEAM_AGE_INJURY,
        (props, queryParams) => <TeamAgeInjuryTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
    pagesMap.set(PagesEnum.TEAM_POWER_RATINGS,
        (props, queryParams) => <TeamPowerRatingsTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
    pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS,
        (props, queryParams) => <TeamFanclubFlagsTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
    pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES,
        (props, queryParams) => <TeamStreakTrophiesTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
    pagesMap.set(PagesEnum.OLDEST_TEAMS,
        (props, queryParams) => <OldestTeamsTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
    pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS,
        (props, queryParams) => <MatchTopHatstatsTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
    pagesMap.set(PagesEnum.MATCH_SURPRISING,
        (props, queryParams) => <MatchSurprisingTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
    pagesMap.set(PagesEnum.MATCH_SPECTATORS,
        (props, queryParams) => <MatchSpectatorsTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />) 
    
    pagesMap.set(PagesEnum.PROMOTIONS,
        (props, queryParams) => <PromotionsTable<LeagueUnitLevelDataProps> levelDataProps={props} queryParams={queryParams} />)

    pagesMap.set(PagesEnum.TEAM_SEARCH, (_props, _queryParams) => <TeamSearchPage />)

    return pagesMap
}
