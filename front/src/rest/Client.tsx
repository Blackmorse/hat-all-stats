import axios from 'axios';
import LeagueData from './models/LeagueData'
import TeamRating from './models/TeamRating'
import LeagueUnitRating from './models/LeagueUnitRating'
import StatisticsParameters from './StatisticsParameters'
import RestTableData from './RestTableData'



export function getLeagueData(leagueId: number, callback: (leagueData: LeagueData) => void): void {
    axios.get<LeagueData>('/api/league/' + leagueId)
        .then(response => response.data)
        .then(callback)
}

export function getTeamRatings(leagueId: number, statisticsParameters: StatisticsParameters, callback: (teamRatings: RestTableData<TeamRating>) => void) {
    let params = new URLSearchParams({
        "page": statisticsParameters.page.toString(),
    }) 

    axios.get<RestTableData<TeamRating>>('/api/league/' + leagueId + '/teamHatstats?' + params.toString())
    .then(response => {
        return response.data
    })
    .then(model => {
        return callback(model)
    })
}

export function getLeagueUnits(leagueId: number, statisticsParameters: StatisticsParameters, callback: (leagueUnits: RestTableData<LeagueUnitRating>) => void) {
    let params = new URLSearchParams({
        "page": statisticsParameters.page.toString(),
    })
    
    axios.get<RestTableData<LeagueUnitRating>>('/api/league/' + leagueId + '/leagueUnits?' + params.toString())
    .then(response => {
        return response.data
    })
    .then(model => {
        return callback(model)
    })
}