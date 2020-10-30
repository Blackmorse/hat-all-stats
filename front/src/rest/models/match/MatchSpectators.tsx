import TeamSortingKey from "../team/TeamSortingKey";

interface MatchSpectators {
    homeTeam: TeamSortingKey,
    awayTeam: TeamSortingKey,
    homeGoals: number,
    awayGoals: number,
    spectators: number,
    matchId: number
}

export default MatchSpectators