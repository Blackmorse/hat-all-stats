import TeamSortingKey from '../team/TeamSortingKey'

interface MatchTopHatstats {
    homeTeam: TeamSortingKey,
    awayTeam: TeamSortingKey,
    homeHatstats: number,
    homeGoals: number,
    awayHatstats: number,
    awayGoals: number,
    matchId: number
}

export default MatchTopHatstats