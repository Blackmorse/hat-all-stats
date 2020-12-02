import LevelData from './LevelData'

interface WorldLoadingInfo {
    proceedCountries: number,
    nextCountry?: [number, string, Date],
    currentCountry?: [number, string]
}

interface WorldData extends LevelData {
    countries: Array<[number, string]>,
    loadingInfo: WorldLoadingInfo
}

export default WorldData