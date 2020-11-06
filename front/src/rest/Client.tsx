import axios from 'axios';
import LeagueData from './models/leveldata/LeagueData'
import DivisionLevelData from './models/leveldata/DivisionLevelData'
import TeamHatstats from './models/team/TeamHatstats'
import LeagueUnitRating from './models/LeagueUnitRating'
import StatisticsParameters, { StatsTypeEnum } from './StatisticsParameters'
import RestTableData from './RestTableData'
import LeagueUnitRequest from './models/request/LeagueUnitRequest'
import DivisionLevelRequest from './models/request/DivisionLevelRequest';
import LeagueRequest from './models/request/LeagueRequest'
import TeamRequest from './models/request/TeamRequest'
import LevelRequest from './models/request/LevelRequest';
import LeagueUnitData from './models/leveldata/LeagueUnitData';
import TeamPosition from './models/TeamPosition';
import TeamData from './models/leveldata/TeamData'
import PlayerStats from './models/PlayerStat'
import TeamRankingsStats from './models/TeamRankingsStats'
import { NearestMatches } from './models/NearestMatch';
import PlayerGoalGames from './models/player/PlayerGoalsGames'
import PlayerCards from './models/player/PlayerCards'
import PlayerSalaryTSI from './models/player/PlayerSalaryTSI'
import PlayerRating from './models/player/PlayerRating'
import PlayerInjury from './models/player/PlayerInjury'
import TeamSalaryTSI from './models/team/TeamSalaryTSI'
import TeamCards from './models/team/TeamCards'
import TeamRating from './models/team/TeamRating'
import TeamAgeInjury from './models/team/TeamAgeInjury'
import TeamGoalPoints from './models/team/TeamGoalPoints'
import TeamPowerRating from './models/team/TeamPowerRating'
import TeamFanclubFlags from './models/team/TeamFanclubFlags'
import TeamStreakTrophies from './models/team/TeamStreakTrophies'
import MatchTopHatstats from './models/match/MatchTopHatstats'
import MatchSpectators from './models/match/MatchSpectators'
import WorldData from './models/leveldata/WorldData';
import TotalOverview from './models/overview/TotalOverview'
import OverviewRequest from './models/request/OverviewRequest'
import AveragesOverview from './models/overview/AveragesOverview'
import NumberOverview from './models/overview/NumberOverview'
import FormationsOverview from './models/overview/FormationsOverview';
import TeamStatOverview from './models/overview/TeamStatOverview';
import PlayerStatOverview from './models/overview/PlayerStatOverview';
import MatchTopHatstatsOverview from './models/overview/MatchTopHatstatsOverview';

export function getLeagueData(leagueId: number, callback: (leagueData: LeagueData) => void): void {
    axios.get<LeagueData>('/api/league/' + leagueId)
        .then(response => response.data)
        .then(callback)
}

export function getDivisionLevelData(leagueId: number, divisionLevel: number, 
        callback: (divisionLevelData: DivisionLevelData) => void): void {
    axios.get<DivisionLevelData>('/api/league/' + leagueId + '/divisionLevel/' + divisionLevel)   
    .then(response => {
        return response.data
    }).then(model => callback(model)) 
}

export function getLeagueUnitData(leagueUnitId: number, callback: (leagueUnitData: LeagueUnitData) => void): void {
    axios.get<LeagueUnitData>('/api/leagueUnit/' + leagueUnitId)
    .then(response => {
        return response.data
    }).then(model => callback(model))
}

export function getTeamData(leagueId: number, callback: (teamData: TeamData) => void): void {
    axios.get<TeamData>('/api/team/' + leagueId)
        .then(response => response.data)
        .then(callback)
}

export function getWorldData(callback: (worldData: WorldData) => void): void {
    axios.get<WorldData>('/api/overview/worldData')
        .then(response => response.data)
        .then(callback)
}

interface LeagueUnitId {
    id: number
}

export function getLeagueUnitIdByName(leagueId: number, leagueUnitName: string, callback: (id: number) => void) {
    axios.get<LeagueUnitId>('/api/league/' + leagueId + '/leagueUnitName/' + leagueUnitName)
        .then(response => response.data)
        .then(leagueUnitId => callback(leagueUnitId.id))
}

function statisticsRequest<T>(path: string): 
    (request: LevelRequest,
        statisticsParameters: StatisticsParameters,
        callback: (entities: RestTableData<T>) => void,
        onError: () => void) => void {
            
            return function(request: LevelRequest,
                statisticsParameters: StatisticsParameters,
                callback: (entities: RestTableData<T>) => void,
                onError: () => void): void {
                    let params = createStatisticsParameters(statisticsParameters)
                    axios.get<RestTableData<T>>(startUrl(request) + '/' + path + '?' + params.toString())
                        .then(response => response.data)
                        .then(entities => callback(entities))
                        .catch(e => onError())
                }
}

export function getTeamRankings(request: LevelRequest, 
        callback: (teamRankingsStats: TeamRankingsStats) => void,
        onError: () => void) {
    axios.get<TeamRankingsStats>(startUrl(request) + '/teamRankings')
        .then(response => response.data)
        .then(entities => callback(entities))
        .catch(e => onError())
}

export function getNearestMatches(request: TeamRequest, 
        callback: (nearestMatches: NearestMatches) => void,
        onError: () => void) {
    axios.get<NearestMatches>('/api/team/' + request.teamId + "/nearestMatches")
        .then(response => response.data)
        .then(nearestMatches => callback(nearestMatches))
        .catch(e => onError())
}

export let getTeamHatstats = statisticsRequest<TeamHatstats>('teamHatstats')

export let getLeagueUnits = statisticsRequest<LeagueUnitRating>('leagueUnits')

export let getTeamPositions = statisticsRequest<TeamPosition>('teamPositions')

export let getPlayerStats = statisticsRequest<PlayerStats>('playerStats')

export let getPlayerGoalsGames = statisticsRequest<PlayerGoalGames>('playerGoalGames')

export let getPlayerCards = statisticsRequest<PlayerCards>('playerCards')

export let getPlayerSalaryTsi = statisticsRequest<PlayerSalaryTSI>('playerTsiSalary')

export let getPlayerRatings = statisticsRequest<PlayerRating>('playerRatings')

export let getPlayerInjuries = statisticsRequest<PlayerInjury>('playerInjuries')

export let getTeamSalaryTSI = statisticsRequest<TeamSalaryTSI>('teamSalaryTsi')

export let getTeamCards = statisticsRequest<TeamCards>('teamCards')

export let getTeamRatings = statisticsRequest<TeamRating>('teamRatings')

export let getTeamAgeInjuries = statisticsRequest<TeamAgeInjury>('teamAgeInjuries')

export let getTeamGoalPoints = statisticsRequest<TeamGoalPoints>('teamGoalPoints')

export let getTeamPowerRatings = statisticsRequest<TeamPowerRating>('teamPowerRatings')

export let getTeamFanclubFlags = statisticsRequest<TeamFanclubFlags>('teamFanclubFlags')

export let getTeamStreakTrophies = statisticsRequest<TeamStreakTrophies>('teamStreakTrophies')

export let getMatchesTopHatstats = statisticsRequest<MatchTopHatstats>('topMatches')

export let getSurprisingMatches = statisticsRequest<MatchTopHatstats>('surprisingMatches')

export let getMatchSpectators = statisticsRequest<MatchSpectators>('matchSpectators')

function requestOverview<T>(path: string):
    (overviewRequest: OverviewRequest, 
    callback: (data: T) => void,
    onError: () => void) => void {

        return function(overviewRequest: OverviewRequest, 
            callback: (data: T) => void,
            onError: () => void) {
                let params = createOverviewParameters(overviewRequest)
                axios.get<T>('/api/overview/' + path + '?' + params)
                    .then(response => response.data)
                    .then(data => callback(data))
                    .catch(e => onError())
            }
    }

export let getTotalOverview = requestOverview<TotalOverview>('totalOverview')

export let getNumberOverview = requestOverview<NumberOverview>('numberOverview')

export let getFormationsOverview = requestOverview<Array<FormationsOverview>>('formations')

export let getAveragesOverview = requestOverview<AveragesOverview>('averagesOverview')

export let getSurprisingMatchesOverview = requestOverview<Array<MatchTopHatstatsOverview>>('surprisingMatches')

export let getTopHatstatsTeamsOverview = requestOverview<Array<TeamStatOverview>>('topHatstatsTeams')

export let getTopSalaryTeamsOverview = requestOverview<Array<TeamStatOverview>>('topSalaryTeams')

export let getTopMatchesOverview = requestOverview<Array<MatchTopHatstatsOverview>>('topMatches')

export let getTopSalaryPlayersOverview = requestOverview<Array<PlayerStatOverview>>('topSalaryPlayers')

export let getTopRatingPlayersOverview = requestOverview<Array<PlayerStatOverview>>('topRatingPlayers')

function startUrl(request: LevelRequest): string {
    if (request.type === 'LeagueRequest') {
        return '/api/league/' + (request as LeagueRequest).leagueId 
    } else if (request.type === 'DivisionLevelRequest') {
        return '/api/league/' + (request as DivisionLevelRequest).leagueId + '/divisionLevel/' 
            + (request as DivisionLevelRequest).divisionLevel
    } else if (request.type === 'LeagueUnitRequest') {
        return '/api/leagueUnit/' + (request as LeagueUnitRequest).leagueUnitId
    } else if(request.type === 'TeamRequest') { 
        return '/api/team/' + (request as TeamRequest).teamId
    } else {
        return ''
    }
}

function createStatisticsParameters(statisticsParameters: StatisticsParameters) {
    var values: any = {}

    values.page = statisticsParameters.page.toString()
    values.pageSize = statisticsParameters.pageSize.toString()
    values.sortBy = statisticsParameters.sortingField
    values.sortDirection = statisticsParameters.sortingDirection
    values.statType = statisticsParameters.statsType.statType
    values.season = statisticsParameters.season

    if(statisticsParameters.statsType.statType === StatsTypeEnum.ROUND) {
        values.statRoundNumber = statisticsParameters.statsType.roundNumber
    }

    return new URLSearchParams(values).toString()
}

function createOverviewParameters(overviewRequest: OverviewRequest) {
    var values: any = {}

    values.season = overviewRequest.season
    values.round = overviewRequest.round
    if(overviewRequest.leagueId) {
        values.leagueId = overviewRequest.leagueId
    }

    return new URLSearchParams(values).toString()
}