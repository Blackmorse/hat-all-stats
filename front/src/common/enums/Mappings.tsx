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
        [PagesEnum.PROMOTIONS, MenuGroupsEnum.PROMOTIONS],
        [PagesEnum.TEAM_SEARCH, MenuGroupsEnum.TEAM_SEARCH],
        [PagesEnum.TEAM_MATCHES, MenuGroupsEnum.MATCH],
        [PagesEnum.DREAM_TEAM, MenuGroupsEnum.PLAYER],
        [PagesEnum.CREATED_SAME_TIME_TEAMS, MenuGroupsEnum.TEAM],
        [PagesEnum.TEAM_COMPARSION, MenuGroupsEnum.TEAM]
        ])
    
    static tacticType: Map<number, string> = 
        new Map([
            [0, 'match.normal'],
            [1, 'match.pressing'],
            [2, 'match.counter_attacks'],
            [3, 'match.attack_in_the_middle'],
            [4, 'match.attack_in_wings'],
            [7, 'match.play_creatively'],
            [8, 'match.long_shots']
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
            ['overview', PagesEnum.OVERVIEW],
            ['dreamTeam', PagesEnum.DREAM_TEAM],
            ['promotions', PagesEnum.PROMOTIONS],
            ['createdSameTimeTeams', PagesEnum.CREATED_SAME_TIME_TEAMS],
            ['teamComparsion', PagesEnum.TEAM_COMPARSION]
        ])

    static roleToTranslationMap: Map<string, string> = 
        new Map([
            ['keeper', 'dream_team.keeper'],
            ['wingback', 'dream_team.wingback'],
            ['defender', 'dream_team.defender'],
            ['winger', 'dream_team.winger'],
            ['midfielder', 'dream_team.midfielder'],
            ['forward', 'dream_team.forward']
        ])
}

export default Mappings