import { MatchRatings } from './TeamMatch'

export default interface SingleMatch {
    homeTeamName: string,
    homeTeamId: number,
    homeGoals: number,
    awayTeamName: string,
    awayTeamId: number,
    awayGoals: number,
    matchId: number,
    homeMatchRatings: MatchRatings,
    awayMatchRatings: MatchRatings
}