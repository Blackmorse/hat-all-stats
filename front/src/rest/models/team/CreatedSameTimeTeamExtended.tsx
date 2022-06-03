import TeamSortingKey from './TeamSortingKey'

export interface CreatedSameTimeTeam {
    hatstats: number,
    attack: number,
    midfield: number,
    defense: number,
    loddarStats: number,
    tsi: number,
    salary: number,
    rating: number,
    ratingEndOfMatch: number,
    age: number,
    injury: number,
    foundedDate: Date,
    powerRating: number,
    teamSortingKey: TeamSortingKey
}

export default interface CreatedSameTimeTeamExtended {
    createdSameTimeTeam: CreatedSameTimeTeam,
    season: number,
    round: number
}

export interface CreatedSameTimeTeamRequest {
    period: 'round' | 'season' | 'weeks'
    weeksNumber?: number
}
