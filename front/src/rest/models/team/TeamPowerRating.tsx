import TeamSortingKey from './TeamSortingKey'

interface TeamPowerRating {
    teamSortingKey: TeamSortingKey,
    powerRating: number
}

export type TeamPowerRatingChart = TeamPowerRating & { season: number, round: number }

export default TeamPowerRating
