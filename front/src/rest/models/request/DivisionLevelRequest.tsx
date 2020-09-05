import LevelRequest from "./LevelRequest";

export default interface DivisionLevelRequest extends LevelRequest {
    type: 'DivisionLevelRequest'
    leagueId: number,
    divisionLevel: number
}