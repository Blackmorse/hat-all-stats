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

export function parseQueryParams(): QueryParams {
    let params = new URLSearchParams(window.location.search);

    let sortingFieldParams = params.get('sortingField')
    let sortingField: string | undefined = undefined
    if (sortingFieldParams !== null) {
        sortingField = sortingFieldParams
    }

    let selectedRowParams = params.get('row')
    let selectedRow: number | undefined = undefined
    if (selectedRowParams !== null) {
        selectedRow = Number(selectedRowParams)
    }
   
    let roundParams = params.get('round')
    let round: number | undefined = undefined
    if(roundParams !== null) {
        round = Number(roundParams)
    }

    let seasonParams = params.get('season')
    let season: number | undefined = undefined
    if(seasonParams !== null) {
        season = Number(seasonParams)
    }

    let teamIdParams = params.get('teamId')
    let teamId: number | undefined = undefined
    if(teamIdParams !== null) {
        teamId = Number(teamIdParams)
    }

        
    let pageStringParams = params.get('page')
    let pageString: string | undefined = undefined
    if(pageStringParams !== null) {
        pageString = pageStringParams
    }

    return {
        sortingField: sortingField,
        selectedRow: selectedRow,
        round: round,
        season: season,
        teamId: teamId,
        pageName: pageString
    }
}

export default QueryParams
