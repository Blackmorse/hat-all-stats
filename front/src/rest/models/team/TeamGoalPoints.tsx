import TeamSortingKey from './TeamSortingKey'

interface TeamGoalPoints {
    teamSortingKey: TeamSortingKey,
    won: number,
    lost: number,
    draw: number,
    goalsFor: number,
    goalsAgaints: number,
    goalsDifference: number,
    points: number
}

export default TeamGoalPoints