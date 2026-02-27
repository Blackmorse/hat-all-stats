import { type JSX } from 'react';
import { PagesEnum } from '../common/enums/PagesEnum';
import DreamTeamPage from '../common/pages/DreamTeamPage';
import PromotionsTable from '../common/pages/PromotionsTable';
import TeamSearchPage from '../common/pages/TeamSearchPage';
import MatchSpectatorsTable from '../common/tables/match/MatchSpectatorsTable';
import MatchSurprisingTable from '../common/tables/match/MatchSurprisingTable';
import MatchTopHatstatsTable from '../common/tables/match/MatchTopHatstatsTable';
import PlayerCardsTable from '../common/tables/player/PlayerCardsTable';
import PlayerGoalGamesTable from '../common/tables/player/PlayerGoalsGamesTable';
import PlayerInjuriesTable from '../common/tables/player/PlayerInjuriesTable';
import PlayerRatingsTable from '../common/tables/player/PlayerRatingsTable';
import PlayerSalaryTsiTable from '../common/tables/player/PlayerSalaryTsiTable';
import OldestTeamsTable from '../common/tables/team/OldestTeamsTable';
import { default as LeagueLevelDataProps, default as LeagueUnitLevelDataProps } from './LeagueUnitLevelDataProps';
import TeamPositionsTable from './TeamPositionsTable';
import TeamCardsSeasonChartAndTable from './TeamCardsSeasonChartAndTable';
import TeamHatstatsSeasonChartAndTable from './TeamHatstatsSeasonChartAndTable';
import { TeamSalaryTsiSeasonChartAndTable } from './TeamSalaryTsiSeasonChartAndTable';
import { TeamRatingsSeasonChartAndTable } from './TeamRatingsSeasonChartAndTable';
import { TeamAgeInjuriesSeasonChartAndTable } from './TeamAgeInjuriesSeasonChartAndTable';
import { TeamPowerRatingsSeasonChartAndTable } from './TeamPowerRatingsSeasonChartAndTable';
import { TeamFanclubFlagsSeasonChartAndTable } from './TeamFanclubFlagsSeasonChartAndTable';
import { TeamStreakTrophiesSeasonChartAndTable } from './TeamStreakTrophiesSeasonChartAndTable';

export default function pages(): Map<PagesEnum, (props: LeagueLevelDataProps) => JSX.Element> {
    const pagesMap = new Map<PagesEnum, (props: LeagueUnitLevelDataProps,) => JSX.Element>()

    pagesMap.set(PagesEnum.TEAM_HATSTATS,
        props => <>
            <TeamPositionsTable levelDataProps={props} />
            <TeamHatstatsSeasonChartAndTable levelDataProps={props} />
        </>)
    pagesMap.set(PagesEnum.DREAM_TEAM, props => <DreamTeamPage<LeagueUnitLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES, props => <PlayerGoalGamesTable<LeagueUnitLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_CARDS, props => <PlayerCardsTable<LeagueUnitLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_SALARY_TSI, props => <PlayerSalaryTsiTable<LeagueUnitLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_RATINGS, props => <PlayerRatingsTable<LeagueUnitLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_INJURIES, props => <PlayerInjuriesTable<LeagueUnitLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_SALARY_TSI, props => <TeamSalaryTsiSeasonChartAndTable levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_CARDS, props => <TeamCardsSeasonChartAndTable levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_RATINGS, props => <TeamRatingsSeasonChartAndTable levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_AGE_INJURY, props => <TeamAgeInjuriesSeasonChartAndTable levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_POWER_RATINGS, props => <TeamPowerRatingsSeasonChartAndTable levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS, props => <TeamFanclubFlagsSeasonChartAndTable levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES, props => <TeamStreakTrophiesSeasonChartAndTable levelDataProps={props} />)
    pagesMap.set(PagesEnum.OLDEST_TEAMS, props => <OldestTeamsTable<LeagueUnitLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, props => <MatchTopHatstatsTable<LeagueUnitLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.MATCH_SURPRISING, props => <MatchSurprisingTable<LeagueUnitLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.MATCH_SPECTATORS, props => <MatchSpectatorsTable<LeagueUnitLevelDataProps> levelDataProps={props} />)

    pagesMap.set(PagesEnum.PROMOTIONS, props => <PromotionsTable<LeagueUnitLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.TEAM_SEARCH, _props => <TeamSearchPage />)

    return pagesMap
}
