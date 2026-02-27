import TeamSortingKey from './TeamSortingKey'

interface TeamRating {
    teamSortingKey: TeamSortingKey,
    rating: number,
    ratingEndOfMatch: number
}

export type TeamRatingChart = TeamRating & { season: number, round: number }

export default TeamRating
