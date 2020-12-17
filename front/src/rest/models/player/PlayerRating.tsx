import PlayerSortingKey from './PlayerSortingKey'

interface PlayerRating {
    playerSortingKey: PlayerSortingKey,
    age: number,
    rating: number,
    ratingEndOfMatch: number,
    role: string
}

export default PlayerRating