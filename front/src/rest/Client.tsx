import axios from 'axios';
import LeagueData from './models/LeagueData'
import TeamRating from './models/TeamRating'
import LeagueUnitRating from './models/LeagueUnitRating'

export function getLeagueData(leagueId: number, callback: (leagueData: LeagueData) => void): void {
    axios.get<LeagueData>('/api/league/' + leagueId)
        .then(response => response.data)
        .then(callback)
}

export function getTeamRatings(leagueId: number, callback: (teamRatings: Array<TeamRating>) => void) {
    axios.get<Array<TeamRating>>('/api/league/' + leagueId + '/teamHatstats')
    .then(response => {
        return response.data
    })
    .then(model => {
        return callback(model)
    })
}

export function getLeagueUnits(leagueId: number, callback: (leagueUnits: Array<LeagueUnitRating>) => void) {
    axios.get<Array<LeagueUnitRating>>('/api/league/' + leagueId + '/leagueUnits')
    .then(response => {
        return response.data
    })
    .then(model => {
        return callback(model)
    })
}