import PlayerSortingKey from './PlayerSortingKey'

interface PlayerRating {
    playerSortingKey: PlayerSortingKey,
    age: number,
    rating: number,
    ratingEndOfMatch: number
}

export default PlayerRating