export interface QueryParams {
    pageName?: string,
    selectedRow?: number,    
    
    season?: number,
    sortingField?: string,
    sortingDirection?: 'asc' | 'desc',
    statType?: string,
    round?: number,
    pageSize?: number,
    pageNumber?: number,
    nationality?: number,
    minAge?: number,
    maxAge?: number,
    role?: string,

    playedAllMatches?: boolean,
    playedInLastMatch?: boolean,
    oneTeamPerUnit?: boolean,
    excludeZeroTsi?: boolean,

    teamId?: number,
    
}

export default QueryParams
