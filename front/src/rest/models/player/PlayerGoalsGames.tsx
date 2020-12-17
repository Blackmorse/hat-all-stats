import PlayerSortingKey from './PlayerSortingKey'

interface PlayerGoalGames {
    playerSortingKey: PlayerSortingKey,
    games: number,
    playedMinutes: number,
    scored: number,
    goalRate: number,
    role: string
}

export default PlayerGoalGames