import { type JSX } from 'react';
import {PagesEnum} from '../common/enums/PagesEnum';
import DreamTeamPage from '../common/pages/DreamTeamPage';
import PromotionsTable from '../common/pages/PromotionsTable';
import TeamSearchPage from '../common/pages/TeamSearchPage';
import Section from '../common/sections/HookSection';
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
import TeamHatstatsTable from '../common/tables/team/TeamHatstatsTable';
import TeamPowerRatingsTable from '../common/tables/team/TeamPowerRatingsTable';
import TeamRatingsTable from '../common/tables/team/TeamRatingsTable';
import TeamSalaryTSITable from '../common/tables/team/TeamSalaryTSITable';
import TeamStreakTrophiesTable from '../common/tables/team/TeamStreakTrophiesTable';
import TeamPositionsChart from './TeamPositionsChart';
import {default as LeagueLevelDataProps, default as LeagueUnitLevelDataProps} from './LeagueUnitLevelDataProps';
import TeamPositionsTable from './TeamPositionsTable';
import i18n from '../i18n'

export default function pages(): Map<PagesEnum, (props: LeagueLevelDataProps) => JSX.Element> {
    const pagesMap = new Map<PagesEnum, (props: LeagueUnitLevelDataProps,) => JSX.Element>()
    pagesMap.set(PagesEnum.TEAM_HATSTATS, 
        props => <>
           <Section  
                element={<TeamPositionsTable levelDataProps={props} />}
                title=''
            />
            <Section
                element={<TeamPositionsChart leagueUnitProps={props}  />}
                title={i18n.t('table.position')}
            />
            <Section
                element={<TeamHatstatsTable<LeagueUnitLevelDataProps> levelDataProps={props} />}
                title=''
            />
            </>)
    pagesMap.set(PagesEnum.DREAM_TEAM, props => <DreamTeamPage<LeagueUnitLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_GOAL_GAMES, props => <PlayerGoalGamesTable<LeagueUnitLevelDataProps> levelDataProps={props} />)
    pagesMap.set(PagesEnum.PLAYER_CARDS, props => <PlayerCardsTable<LeagueUnitLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.PLAYER_SALARY_TSI, props => <PlayerSalaryTsiTable<LeagueUnitLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.PLAYER_RATINGS, props => <PlayerRatingsTable<LeagueUnitLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.PLAYER_INJURIES, props => <PlayerInjuriesTable<LeagueUnitLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_SALARY_TSI, props => <TeamSalaryTSITable<LeagueUnitLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_CARDS, props => <TeamCardsTable<LeagueUnitLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_RATINGS, props => <TeamRatingsTable<LeagueUnitLevelDataProps> levelDataProps={props}  />)  
    pagesMap.set(PagesEnum.TEAM_AGE_INJURY, props => <TeamAgeInjuryTable<LeagueUnitLevelDataProps> levelDataProps={props}  />) 
    pagesMap.set(PagesEnum.TEAM_POWER_RATINGS, props => <TeamPowerRatingsTable<LeagueUnitLevelDataProps> levelDataProps={props}  />) 
    pagesMap.set(PagesEnum.TEAM_FANCLUB_FLAGS, props => <TeamFanclubFlagsTable<LeagueUnitLevelDataProps> levelDataProps={props}  />) 
    pagesMap.set(PagesEnum.TEAM_STREAK_TROPHIES, props => <TeamStreakTrophiesTable<LeagueUnitLevelDataProps> levelDataProps={props}  />) 
    pagesMap.set(PagesEnum.OLDEST_TEAMS, props => <OldestTeamsTable<LeagueUnitLevelDataProps> levelDataProps={props}  />) 
    pagesMap.set(PagesEnum.MATCH_TOP_HATSTATS, props => <MatchTopHatstatsTable<LeagueUnitLevelDataProps> levelDataProps={props}  />) 
    pagesMap.set(PagesEnum.MATCH_SURPRISING, props => <MatchSurprisingTable<LeagueUnitLevelDataProps> levelDataProps={props}  />) 
    pagesMap.set(PagesEnum.MATCH_SPECTATORS, props => <MatchSpectatorsTable<LeagueUnitLevelDataProps> levelDataProps={props}  />) 
    
    pagesMap.set(PagesEnum.PROMOTIONS, props => <PromotionsTable<LeagueUnitLevelDataProps> levelDataProps={props}  />)
    pagesMap.set(PagesEnum.TEAM_SEARCH, _props => <TeamSearchPage />)

    return pagesMap
}
