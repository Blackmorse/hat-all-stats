export interface LeagueUnitTeamStat {
    position: number,
    teamId: number,
    teamName: string,
    games: number,
    scored: number,
    missed: number,
    win: number,
    draw: number,
    lost: number,
    points: number
}

export default interface LeagueUnitTeamStatsWithPositionDiff {
    positionDiff: number, 
    leagueUnitTeamStat: LeagueUnitTeamStat
}

export interface LeagueUnitTeamStatHistoryInfo {
    teamsLastRoundWithPositionsDiff: Array<LeagueUnitTeamStatsWithPositionDiff>,
    positionsHistory: Array<LeagueUnitTeamStat>
}