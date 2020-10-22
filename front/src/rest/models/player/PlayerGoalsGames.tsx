import PlayerSortingKey from './PlayerSortingKey'

interface PlayerGoalGames {
    playerSortingKey: PlayerSortingKey,
    games: number,
    playedMinutes: number,
    scored: number,
    goalRate: number
}

export default PlayerGoalGames