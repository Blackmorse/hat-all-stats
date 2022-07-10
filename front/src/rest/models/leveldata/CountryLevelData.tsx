import LevelData from './LevelData'

export interface LoadingInfo {
    loadingInfo: string,
    date?: Date
}

interface CountryLevelData extends LevelData {
    leagueId: number,
    leagueName: string,
    loadingInfo: LoadingInfo
}

export default CountryLevelData
