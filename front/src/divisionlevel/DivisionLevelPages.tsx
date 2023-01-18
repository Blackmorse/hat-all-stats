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
import PromotionsTable from '../common/pages/PromotionsTable';
import DreamTeamPage from '../common/pages/DreamTeamPage';
import OldestTeamsTable from '../common/tables/team/OldestTeamsTable';
import TeamSearchPage from '../common/pages/TeamSearchPage';

export default function pages(): Map<PagesEnum, (props: DivisionLevelDataProps) => JSX.Element> {

    const pagesMap = new Map<PagesEnum, (props: DivisionLevelDataProps) => JSX.Element>()
    pagesMap.set(PagesEnum.OVERVIEW, props => <DivisionLevelOverviewPage levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_HATSTATS, props => <TeamHatstatsTable<DivisionLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.LEAGUE_UNITS, props => <LeagueUnitsTable<DivisionLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.DREAM_TEAM, props => <DreamTeamPage<DivisionLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES, props => <PlayerGoalsGamesTable<DivisionLevelDataProps> levelDataProps={props} />) 
    pagesMap.set(PagesEnum.PLAYER_CARDS, props => <PlayerCardsTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.PLAYER_SALARY_TSI, props => <PlayerSalaryTsiTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.PLAYER_RATINGS, props => <PlayerRatingsTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.PLAYER_INJURIES, props => <PlayerInjuriesTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_SALARY_TSI, props => <TeamSalaryTSITable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_CARDS, props => <TeamCardsTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_RATINGS, props => <TeamRatingsTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_AGE_INJURY, props => <TeamAgeInjuryTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_GOAL_POINTS, props => <TeamGoalPointsTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_POWER_RATINGS, props => <TeamPowerRatingsTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS, props => <TeamFanclubFlagsTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES, props => <TeamStreakTrophiesTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.OLDEST_TEAMS, props => <OldestTeamsTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, props => <MatchTopHatstatsTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.MATCH_SURPRISING, props => <MatchSurprisingTable<DivisionLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.MATCH_SPECTATORS, props => <MatchSpectatorsTable<DivisionLevelDataProps> levelDataProps={props}  />)

    pagesMap.set(PagesEnum.PROMOTIONS, props => <PromotionsTable<DivisionLevelDataProps> levelDataProps={props}  />)

    pagesMap.set(PagesEnum.TEAM_SEARCH, _props => <TeamSearchPage />)
    return pagesMap
}
