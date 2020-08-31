export enum SortingDirection {
    ASC = "asc",
    DESC = "desc"
}

export enum StatsTypeEnum {
    AVG = "avg",
    MAX = "max",
    ACCUMULATE = "accumulate",
    ROUND = "statRound"
}

export interface StatsType {
    statType: StatsTypeEnum,
    roundNumber?: number
}


export default interface StatisticsParameters {
    page: number,
    pageSize: number,
    sortingField: string,
    sortingDirection: SortingDirection,
    statsType: StatsType,
    season: number
}