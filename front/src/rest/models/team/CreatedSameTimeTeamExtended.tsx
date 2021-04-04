import TeamSortingKey from './TeamSortingKey'

interface CreatedSameTimeTeam {
    foundedDate: number,
    powerRating: number,
    teamSortingKey: TeamSortingKey
}

export default interface CreatedSameTimeTeamExtended {
    createdSameTimeTeam: CreatedSameTimeTeam,
    season: number,
    round: number
}