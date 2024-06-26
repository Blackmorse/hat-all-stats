interface TeamRanking {
    teamId: number,
    teamName: string,
    divisionLevel: number,
    season: number,
    round: number,
    rankType: string,
    hatstats: number,
    hatstatsPosition: number,
    attack: number,
    attackPosition: number,
    midfield: number,
    midfieldPosition: number,
    defense: number,
    defensePosition: number,
    loddarStats: number,
    loddarStatsPosition: number,
    tsi: number,
    tsiPosition: number,
    salary: number,
    salaryPosition: number,
    rating: number,
    ratingPosition: number,
    ratingEndOfMatch: number,
    ratingEndOfMatchPosition: number,
    age: number,
    agePosition: number,
    injury: number,
    injuryPosition: number,
    injuryCount: number,
    injuryCountPosition: number,
    powerRating: number,
    powerRatingPosition: number,
    founded: Date,
    foundedPosition: number
}

export default TeamRanking