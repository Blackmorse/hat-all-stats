import { type JSX } from 'react'
import WorldOverviewPage from './WorldOverviewPage'
import WorldLevelDataProps from './WorldLevelDataProps'
import { PagesEnum } from '../common/enums/PagesEnum'
import PlayerSalaryTsiTable from '../common/tables/player/PlayerSalaryTsiTable';
import TeamHatstatsTable from '../common/tables/team/TeamHatstatsTable';
import MatchTopHatstatsTable from '../common/tables/match/MatchTopHatstatsTable';
import MatchSurprisingTable from '../common/tables/match/MatchSurprisingTable';
import DreamTeamPage from '../common/pages/DreamTeamPage';
import OldestTeamsTable from '../common/tables/team/OldestTeamsTable';
import TeamSearchPage from '../common/pages/TeamSearchPage'
import TeamSalaryTSITable from '../common/tables/team/TeamSalaryTSITable'
import PlayerCardsTable from '../common/tables/player/PlayerCardsTable';
import PlayerRatingsTable from '../common/tables/player/PlayerRatingsTable';
import PlayerGoalsGamesTable from '../common/tables/player/PlayerGoalsGamesTable';
import PlayerInjuriesTable from '../common/tables/player/PlayerInjuriesTable';
import TeamCardsTable from '../common/tables/team/TeamCardsTable';
import TeamRatingsTable from '../common/tables/team/TeamRatingsTable';
import TeamAgeInjuryTable from '../common/tables/team/TeamAgeInjuryTable';
import TeamGoalPointsTable from '../common/tables/team/TeamGoalPointsTable';
import TeamPowerRatingsTable from '../common/tables/team/TeamPowerRatingsTable';
import TeamFanclubFlagsTable from '../common/tables/team/TeamFanclubFlagsTable';
import TeamStreakTrophiesTable from '../common/tables/team/TeamStreakTrophiesTable';
import MatchSpectatorsTable from '../common/tables/match/MatchSpectatorsTable';


export default function pages() {
    const pagesMap = new Map<PagesEnum, (props: WorldLevelDataProps) => JSX.Element>()
    pagesMap.set(PagesEnum.OVERVIEW, props => <WorldOverviewPage levelDataProps={props} />)

    pagesMap.set(PagesEnum.TEAM_HATSTATS, props => <TeamHatstatsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.TEAM_SALARY_TSI, props => <TeamSalaryTSITable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.TEAM_CARDS, props => <TeamCardsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.TEAM_RATINGS, props => <TeamRatingsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.TEAM_AGE_INJURY, props => <TeamAgeInjuryTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.TEAM_GOAL_POINTS, props => <TeamGoalPointsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.TEAM_POWER_RATINGS, props => <TeamPowerRatingsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS, props => <TeamFanclubFlagsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES, props => <TeamStreakTrophiesTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.OLDEST_TEAMS, props => <OldestTeamsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.DREAM_TEAM, props => <DreamTeamPage<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)

    pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES, props => <PlayerGoalsGamesTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.PLAYER_CARDS, props => <PlayerCardsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.PLAYER_SALARY_TSI, props => <PlayerSalaryTsiTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.PLAYER_RATINGS, props => <PlayerRatingsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.PLAYER_INJURIES, props => <PlayerInjuriesTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)

    pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, props => <MatchTopHatstatsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.MATCH_SURPRISING, props => <MatchSurprisingTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)
    pagesMap.set(PagesEnum.MATCH_SPECTATORS, props => <MatchSpectatorsTable<WorldLevelDataProps> levelDataProps={props}  showCountryFlags={true} />)

    pagesMap.set(PagesEnum.TEAM_SEARCH, _props => <TeamSearchPage />)
    return pagesMap
}
