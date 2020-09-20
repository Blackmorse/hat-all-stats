import LevelRequest from './LevelRequest'

export default interface LeagueUnitRequest extends LevelRequest {
    type: 'LeagueUnitRequest'
    leagueUnitId: number
}