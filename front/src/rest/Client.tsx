import axios from 'axios';
import LeagueData from './models/LeagueData'
import DivisionLevelData from './models/DivisionLevelData'
import TeamRating from './models/TeamRating'
import LeagueUnitRating from './models/LeagueUnitRating'
import StatisticsParameters, { StatsTypeEnum } from './StatisticsParameters'
import RestTableData from './RestTableData'
import LeagueUnitRequest from './models/request/LeagueUnitRequest'
import DivisionLevelRequest from './models/request/DivisionLevelRequest';
import LeagueRequest from './models/request/LeagueRequest'
import TeamRequest from './models/request/TeamRequest'
import LevelRequest from './models/request/LevelRequest';
import LeagueUnitData from './models/LeagueUnitData';
import TeamPosition from './models/TeamPosition';
import TeamData from './models/TeamData'
import PlayerStats from './models/PlayerStat'
import TeamRankingsStats from './models/TeamRankingsStats'
import { NearestMatches } from './models/NearestMatch';
import PlayerGoalGames from './models/player/PlayerGoalsGames'
import PlayerCards from './models/player/PlayerCards'
import PlayerSalaryTSI from './models/player/PlayerSalaryTSI'

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

export function getTeamRatings(request: LevelRequest, 
        statisticsParameters: StatisticsParameters, 
        callback: (teamRatings: RestTableData<TeamRating>) => void,
        onError: () => void) {
    let params = createParameters(statisticsParameters)    

    axios.get<RestTableData<TeamRating>>(startUrl(request) + '/teamHatstats?' + params.toString())
    .then(response => response.data)
    .then(model => callback(model))
    .catch(e => onError())
}

export function getLeagueUnits(request: LevelRequest,
        statisticsParameters: StatisticsParameters, 
        callback: (leagueUnits: RestTableData<LeagueUnitRating>) => void,
        onError: () => void) {
    let params = createParameters(statisticsParameters) 
    
    axios.get<RestTableData<LeagueUnitRating>>(startUrl(request) + '/leagueUnits?' + params.toString())
    .then(response => response.data)
    .then(model =>  callback(model))
    .catch(e => onError())
}

export function getTeamPositions(request: LeagueUnitRequest, 
        statisticsParameters: StatisticsParameters,
        callback: (teamPositions: RestTableData<TeamPosition>) => void,
        onError: () => void) {
    let params = createParameters(statisticsParameters)

    axios.get<RestTableData<TeamPosition>>(startUrl(request) + '/teamPositions?' + params.toString())
        .then(response => response.data)
        .then(model => callback(model))
        .catch(e => onError())
}

export function getTeamData(leagueId: number, callback: (teamData: TeamData) => void): void {
    axios.get<TeamData>('/api/team/' + leagueId)
        .then(response => response.data)
        .then(callback)
}

export function getPlayerStats(request: LevelRequest, 
        statisticsParameters: StatisticsParameters,
        callback: (playerStats: RestTableData<PlayerStats>) => void,
        onError: () => void)  {
    let params = createParameters(statisticsParameters)
    axios.get<RestTableData<PlayerStats>>(startUrl(request) + '/playerStats?' + params.toString())
        .then(response => response.data)
        .then(model => callback(model))
        .catch(e => onError())
    }

export function getTeamRankings(request: LevelRequest, 
        callback: (teamRankingsStats: TeamRankingsStats) => void,
        onError: () => void) {
    axios.get<TeamRankingsStats>(startUrl(request) + '/teamRankings')
        .then(response => response.data)
        .then(entities => callback(entities))
        .catch(e => onError())
}

interface LeagueUnitId {
    id: number
}

export function getLeagueUnitIdByName(leagueId: number, leagueUnitName: string, callback: (id: number) => void) {
    axios.get<LeagueUnitId>('/api/league/' + leagueId + '/leagueUnitName/' + leagueUnitName)
        .then(response => response.data)
        .then(leagueUnitId => callback(leagueUnitId.id))
}

export function getNearestMatches(request: TeamRequest, 
        callback: (nearestMatches: NearestMatches) => void,
        onError: () => void) {
    axios.get<NearestMatches>('/api/team/' + request.teamId + "/nearestMatches")
        .then(response => response.data)
        .then(nearestMatches => callback(nearestMatches))
        .catch(e => onError())
}

export function getPlayerGoalsGames(request: LevelRequest, 
        statisticsParameters: StatisticsParameters,
        callback: (playerStats: RestTableData<PlayerGoalGames>) => void,
        onError: () => void) {
    let params = createParameters(statisticsParameters)
    axios.get<RestTableData<PlayerGoalGames>>(startUrl(request) + '/playerGoalGames?' + params.toString())
        .then(response => response.data)
        .then(playerGoalGames => callback(playerGoalGames))
        .catch(e => onError())
}

export function getPlayerCards(request: LevelRequest,
        statisticsParameters: StatisticsParameters,
        callback: (playerCards: RestTableData<PlayerCards>) => void,
        onError: () => void) {
    let params = createParameters(statisticsParameters)
    axios.get<RestTableData<PlayerCards>>(startUrl(request) + '/playerCards?' + params.toString())
        .then(response => response.data)
        .then(playerCards => callback(playerCards))
        .catch(e => onError())
}

export function getPlayerSalaryTsi(request: LevelRequest,
        statisticsParameters: StatisticsParameters,
        callback: (playerSalaryTsis: RestTableData<PlayerSalaryTSI>) => void,
        onError: () => void) {
    let params = createParameters(statisticsParameters)
    axios.get<RestTableData<PlayerSalaryTSI>>(startUrl(request) + '/playerTsiSalary?' + params.toString())
    .then(response => response.data)
    .then(playerSalaryTsis => callback(playerSalaryTsis))
    .catch(e => onError())
    }

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

function createParameters(statisticsParameters: StatisticsParameters) {
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