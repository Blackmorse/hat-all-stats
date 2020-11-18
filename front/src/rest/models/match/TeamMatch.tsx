import TeamSortingKey from '../team/TeamSortingKey'

export interface MatchRatings {
    formation: String,
    tacticType: number,
    tacticSkill: number,
    ratingMidfield: number,
    ratingRightDef: number,
    ratingMidDef: number,
    ratingLeftDef: number,
    ratingRightAtt: number,
    ratingMidAtt: number,
    ratingLeftAtt: number
}

interface TeamMatch {
    season: number,
    date: Date,
    round: number,
    homeTeam: TeamSortingKey,
    awayTeam: TeamSortingKey,
    matchId: number,
    homegoals: number,
    awayGoals: number,
    homeMatchRatings: MatchRatings,
    awayMatchRatings: MatchRatings
}


 
export default TeamMatch