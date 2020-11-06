export default interface NearestMatch {
    matchDate: Date,
    status: string,
    homeTeamId: number,
    homeTeamName: string,
    homeGoals: number,
    awayGoals: number,
    awayTeamName: string,
    awayTeamId: number,
    matchId: number
}

export interface NearestMatches {
    playedMatches: Array<NearestMatch>,
    upcomingMatches: Array<NearestMatch>
}