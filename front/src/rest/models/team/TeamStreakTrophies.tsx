import TeamSortingKey from './TeamSortingKey'

interface TeamStreakTrophies {
    teamSortingKey: TeamSortingKey,
    trophiesNumber: number,
    numberOfVictories: number,
    numberOfUndefeated: number
}

export type TeamStreakTrophiesChart = TeamStreakTrophies & { season: number, round: number }

export default TeamStreakTrophies
