interface OverviewMatchAverages {
    hatstats: number,
    spectators: number,
    goals: number
}

interface OverviewTeamPlayerAverages {
    age: number,
    salary: number,
    rating: number
}

interface AveragesOverview { 
    matchAverages: OverviewMatchAverages,
    teamPlayerAverages: OverviewTeamPlayerAverages
}

export default AveragesOverview