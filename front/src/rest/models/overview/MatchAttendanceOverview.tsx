import TeamSortingKey from '../team/TeamSortingKey'

interface MatchAttendanceOverview {
    leagueId: number,
    homeTeams: TeamSortingKey,
    awayTeam: TeamSortingKey,
    homeGoals: number,
    awayGoals: number,
    matchId: number,
    spectators: number
}

export default MatchAttendanceOverview