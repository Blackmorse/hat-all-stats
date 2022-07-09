import LevelRequest from './LevelRequest'

export default interface PlayerRequest extends LevelRequest {
    type: 'PlayerRequest'
    playerId: number
}
