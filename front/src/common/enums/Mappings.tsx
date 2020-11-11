import { PagesEnum } from './PagesEnum'
import { MenuGroupsEnum } from './MenuGroupsEnum'
import { BiDirectionalMap } from 'bi-directional-map/dist';

class Mappings {
    static groupMap: Map<PagesEnum, MenuGroupsEnum> = 
        new Map([[PagesEnum.LEAGUE_UNITS, MenuGroupsEnum.LEAGUE],
        [PagesEnum.TEAM_HATSTATS, MenuGroupsEnum.TEAM],
        [PagesEnum.TEAM_OVERVIEW, MenuGroupsEnum.TEAM],
        [PagesEnum.PLAYER_GOAL_GAMES, MenuGroupsEnum.PLAYER],
        [PagesEnum.PLAYER_CARDS, MenuGroupsEnum.PLAYER],
        [PagesEnum.PLAYER_SALARY_TSI, MenuGroupsEnum.PLAYER],
        [PagesEnum.PLAYER_RATINGS, MenuGroupsEnum.PLAYER],
        [PagesEnum.PLAYER_INJURIES, MenuGroupsEnum.PLAYER],
        [PagesEnum.TEAM_SALARY_TSI, MenuGroupsEnum.TEAM],
        [PagesEnum.TEAM_CARDS, MenuGroupsEnum.TEAM],
        [PagesEnum.TEAM_RATINGS, MenuGroupsEnum.TEAM],
        [PagesEnum.TEAM_AGE_INJURY, MenuGroupsEnum.TEAM],
        [PagesEnum.TEAM_GOAL_POINTS, MenuGroupsEnum.TEAM],
        [PagesEnum.TEAM_POWER_RATINGS, MenuGroupsEnum.TEAM],
        [PagesEnum.TEAM_FANCLUB_FLAGS, MenuGroupsEnum.TEAM],
        [PagesEnum.TEAM_STREAK_TROPHIES, MenuGroupsEnum.TEAM],
        [PagesEnum.MATCH_TOP_HATSTATS, MenuGroupsEnum.MATCH],
        [PagesEnum.MATCH_SURPRISING, MenuGroupsEnum.MATCH],
        [PagesEnum.MATCH_SPECTATORS, MenuGroupsEnum.MATCH],
        [PagesEnum.OVERVIEW, MenuGroupsEnum.OVERVIEW],
        [PagesEnum.PROMOTIONS, MenuGroupsEnum.PROMOTIONS]
        ])

    static PAGE = 'page'

    static queryParamToPageMap: BiDirectionalMap<string, PagesEnum> = 
        new BiDirectionalMap([
            ['leagueUnits', PagesEnum.LEAGUE_UNITS],
            ['teamHatstats', PagesEnum.TEAM_HATSTATS],
            ['teamOverview', PagesEnum.TEAM_OVERVIEW],
            ['playerGoalGames', PagesEnum.PLAYER_GOAL_GAMES],
            ['playerCards', PagesEnum.PLAYER_CARDS],
            ['playerSalaryTsi', PagesEnum.PLAYER_SALARY_TSI],
            ['playerRatings', PagesEnum.PLAYER_RATINGS],
            ['playerInjuries', PagesEnum.PLAYER_INJURIES],
            ['teamSalaryTsi', PagesEnum.TEAM_SALARY_TSI],
            ['teamCards', PagesEnum.TEAM_CARDS],
            ['teamRatings', PagesEnum.TEAM_RATINGS],
            ['teamAgeInjury', PagesEnum.TEAM_AGE_INJURY],
            ['teamGoalPoints', PagesEnum.TEAM_GOAL_POINTS],
            ['teamPowerRatings', PagesEnum.TEAM_POWER_RATINGS],
            ['teamFanclubFlags', PagesEnum.TEAM_FANCLUB_FLAGS],
            ['teamStreakTrophies', PagesEnum.TEAM_STREAK_TROPHIES],
            ['matchTopHatstats', PagesEnum.MATCH_TOP_HATSTATS],
            ['matchSurprising', PagesEnum.MATCH_SURPRISING],
            ['matchSpectators', PagesEnum.MATCH_SPECTATORS],
            ['overview', PagesEnum.OVERVIEW]
        ])
}

export default Mappings