import TeamRanking from './TeamRanking'

interface TeamRankingsStats {
    teamRankings: Array<TeamRanking>, 
    leagueTeamsCounts: Array<[number, number]>, 
    divisionLevelTeamsCounts: Array<[number, number]>,
    currencyRate: number,
    currencyName: string
}

export default TeamRankingsStats