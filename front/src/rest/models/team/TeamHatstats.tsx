import TeamSortingKey from "./TeamSortingKey";

interface TeamHatstats {
    teamSortingKey: TeamSortingKey,
    hatStats: number, 
    midfield: number, 
    defense: number, 
    attack: number,
    loddarStats: number
}

export type TeamHatstatsChart = TeamHatstats & { season: number, round: number }

export default TeamHatstats
