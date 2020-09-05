import LevelRequest from './LevelRequest'

export default interface LeagueRequest extends LevelRequest {
    type: 'LeagueRequest'
    leagueId: number
}