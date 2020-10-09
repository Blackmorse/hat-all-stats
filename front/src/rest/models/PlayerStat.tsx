interface PlayerStat {
    playerId: number,
    firstName: string,
    lastName: string,
    teamId: number,
    teamName: String,
    leagueUnitId: number,
    leagueUnitName: string,
    age: number,
    games: number,
    played: number,
    scored: number,
    yellowCards: number,
    redCards: number,
    totalInjuries: number,
    goalRate: number
}

export default PlayerStat