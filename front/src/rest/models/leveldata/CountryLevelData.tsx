import LevelData from './LevelData'

interface LoadingInfo {
    loadingInfo: string,
    date?: Date
}

interface CountryLevelData extends LevelData {
    leagueId: number,
    leagueName: string,
    loadingInfo: LoadingInfo
}

export default CountryLevelData