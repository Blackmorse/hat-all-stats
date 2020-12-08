import PlayerSortingKey from './PlayerSortingKey'

interface DreamTeamPlayer {
    playerSortingKey: PlayerSortingKey,
    round: number,
    role: string,
    rating: number,
    ratingEndOfMatch: number
}

export default DreamTeamPlayer