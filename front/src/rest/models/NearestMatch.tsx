export default interface NearestMatch {
    matchDate: Date,
    status: string,
    homeTeamId: number,
    homeTeamName: number,
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