import React from 'react';
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
import TeamPowerRatingsTable from '../common/tables/team/TeamPowerRatingsTable';
import TeamFanclubFlagsTable from '../common/tables/team/TeamFanclubFlagsTable';
import TeamStreakTrophiesTable from '../common/tables/team/TeamStreakTrophiesTable';
import MatchTopHatstatsTable from '../common/tables/match/MatchTopHatstatsTable';
import MatchSurprisingTable from '../common/tables/match/MatchSurprisingTable';
import MatchSpectatorsTable from '../common/tables/match/MatchSpectatorsTable';
import OldestTeamsTable from '../common/tables/team/OldestTeamsTable'
import PromotionsTable from '../common/pages/PromotionsTable'
import DreamTeamPage from '../common/pages/DreamTeamPage';
import TeamSearchPage from '../common/pages/TeamSearchPage';
import HookTeamGoalPointsTable from '../common/tables/team/TeamGoalPointsTable';

export default function pages(): Map<PagesEnum, (props: LeagueLevelDataProps) => JSX.Element> {
    const pagesMap = new Map<PagesEnum, (props: LeagueLevelDataProps) => JSX.Element>()
    pagesMap.set(PagesEnum.OVERVIEW, 
        props => <LeagueOverviewPage levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_HATSTATS, props => <TeamHatstatsTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.LEAGUE_UNITS, props => <LeagueUnitsTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.DREAM_TEAM, props => <DreamTeamPage<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES, props => <PlayerGoalGamesTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_CARDS, props => <PlayerCardsTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_SALARY_TSI, props => <PlayerSalaryTsiTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_RATINGS, props => <PlayerRatingsTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_INJURIES, props => <PlayerInjuriesTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_SALARY_TSI, props => <TeamSalaryTSITable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_CARDS,  props => <TeamCardsTable<LeagueLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_RATINGS, props => <TeamRatingsTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_AGE_INJURY, props => <TeamAgeInjuryTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_GOAL_POINTS, props => <HookTeamGoalPointsTable<LeagueLevelDataProps> levelDataProps={props}/>)
    pagesMap.set(PagesEnum.TEAM_POWER_RATINGS, props => <TeamPowerRatingsTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS, props => <TeamFanclubFlagsTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES, props => <TeamStreakTrophiesTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.OLDEST_TEAMS, props => <OldestTeamsTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, props => <MatchTopHatstatsTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.MATCH_SURPRISING, props => <MatchSurprisingTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.MATCH_SPECTATORS, props => <MatchSpectatorsTable<LeagueLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PROMOTIONS, props => <PromotionsTable<LeagueLevelDataProps> levelDataProps={props} />)

    pagesMap.set(PagesEnum.TEAM_SEARCH, _props => <TeamSearchPage />)
    return pagesMap
}
