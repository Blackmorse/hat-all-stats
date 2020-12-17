import PlayerSortingKey from './PlayerSortingKey'

interface PlayerCards {
    playerSortingKey: PlayerSortingKey,
    games: number,
    playedMinutes: number,
    yellowCards: number,
    redCards: number,
    role: string
}

export default PlayerCards