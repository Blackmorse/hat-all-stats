import axios from 'axios';
import LeagueData from './models/LeagueData'
import DivisionLevelData from './models/DivisionLevelData'
import TeamRating from './models/TeamRating'
import LeagueUnitRating from './models/LeagueUnitRating'
import StatisticsParameters, { StatsTypeEnum } from './StatisticsParameters'
import RestTableData from './RestTableData'
import DivisionLevelRequest from './models/request/DivisionLevelRequest';
import LeagueRequest from './models/request/LeagueRequest'
import LevelRequest from './models/request/LevelRequest';

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

export function getTeamRatings(request: LevelRequest, 
        statisticsParameters: StatisticsParameters, 
        callback: (teamRatings: RestTableData<TeamRating>) => void) {
    let params = createParameters(statisticsParameters)    

    axios.get<RestTableData<TeamRating>>(startUrl(request) + '/teamHatstats?' + params.toString())
    .then(response => {
        return response.data
    })
    .then(model => {
        return callback(model)
    })
}

export function getLeagueUnits(request: LevelRequest,
        statisticsParameters: StatisticsParameters, 
        callback: (leagueUnits: RestTableData<LeagueUnitRating>) => void) {
    let params = createParameters(statisticsParameters) 
    
    axios.get<RestTableData<LeagueUnitRating>>(startUrl(request) + '/leagueUnits?' + params.toString())
    .then(response => {
        return response.data
    })
    .then(model => {
        return callback(model)
    })
}

function startUrl(request: LevelRequest): string {
    if (request.type === 'LeagueRequest') {
        return '/api/league/' + (request as LeagueRequest).leagueId 
    } else if (request.type === 'DivisionLevelRequest') {
        return '/api/league/' + (request as DivisionLevelRequest).leagueId + '/divisionLevel/' 
            + (request as DivisionLevelRequest).divisionLevel
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