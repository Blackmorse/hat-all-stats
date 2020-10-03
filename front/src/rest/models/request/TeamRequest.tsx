import LevelRequest from './LevelRequest'

export default interface TeamRequest extends LevelRequest {
    type: 'TeamRequest'
    teamId: number
}