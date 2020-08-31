import axios from 'axios';
import LeagueData from './models/LeagueData'
import TeamRating from './models/TeamRating'
import LeagueUnitRating from './models/LeagueUnitRating'
import StatisticsParameters, { StatsTypeEnum } from './StatisticsParameters'
import RestTableData from './RestTableData'

export function getLeagueData(leagueId: number, callback: (leagueData: LeagueData) => void): void {
    axios.get<LeagueData>('/api/league/' + leagueId)
        .then(response => response.data)
        .then(callback)
}

export function getTeamRatings(leagueId: number, statisticsParameters: StatisticsParameters, callback: (teamRatings: RestTableData<TeamRating>) => void) {
    let params = createParameters(statisticsParameters)    

    axios.get<RestTableData<TeamRating>>('/api/league/' + leagueId + '/teamHatstats?' + params.toString())
    .then(response => {
        return response.data
    })
    .then(model => {
        return callback(model)
    })
}

export function getLeagueUnits(leagueId: number, statisticsParameters: StatisticsParameters, callback: (leagueUnits: RestTableData<LeagueUnitRating>) => void) {
    let params = createParameters(statisticsParameters) 
    
    axios.get<RestTableData<LeagueUnitRating>>('/api/league/' + leagueId + '/leagueUnits?' + params.toString())
    .then(response => {
        return response.data
    })
    .then(model => {
        return callback(model)
    })
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