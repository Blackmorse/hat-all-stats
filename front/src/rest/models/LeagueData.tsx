interface LeagueData {
    leagueId: number,
    leagueName: string,
    divisionLevels: Array<string>,
    currentRound: number,
    rounds: Array<number>
}

export default LeagueData