import TeamSortingKey from './TeamSortingKey'

interface TeamCards {
    teamSortingKey: TeamSortingKey, 
    yellowCards: number, 
    redCards: number
}

export type TeamCardsChart = TeamCards & { season: number, round: number }

export default TeamCards
