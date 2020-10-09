import TeamRanking from './TeamRanking'

interface TeamRankingsStats {
    teamRankings: Array<TeamRanking>, 
    leagueTeamsCount: number, 
    divisionLevelTeamsCount: number,
    currencyRate: number,
    currencyName: string
}

export default TeamRankingsStats