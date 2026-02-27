import TeamSortingKey from './TeamSortingKey'

interface TeamAgeInjury {
    teamSortingKey: TeamSortingKey,
    age: number,
    injury: number,
    injuryCount: number
}

export type TeamAgeInjuryChart = TeamAgeInjury & { season: number, round: number }

export default TeamAgeInjury
