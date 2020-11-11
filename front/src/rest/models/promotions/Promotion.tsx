export interface PromoteTeam {
    teamId: number, 
    teamName: string, 
    divisionLevel: number, 
    leagueUnitId: number,                   
    leagueUnitName: string, 
    position: number, 
    points: number, 
    diff: number, 
    scored: number
}

export interface Promotion {
    season: number, 
    leagueId: number, 
    upDivisionLevel: number, 
    promoteType: string,
    downTeams: Array<PromoteTeam>, 
    upTeams: Array<PromoteTeam>
}

interface PromotionWithType {
    upDivisionLevel: number, 
    upDivisionLevelName: string,
    downDivisionLevelName: string,
    promoteType: String, 
    promotions: Array<Promotion>
}

export default PromotionWithType