export interface TeamPosition {
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

export default interface TeamPositionWithDiff {
    positionDiff: number, 
    leagueUnitTeamStat: TeamPosition
}