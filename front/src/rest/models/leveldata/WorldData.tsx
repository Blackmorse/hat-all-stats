import LevelData from './LevelData'

export interface WorldLoadingInfo {
    proceedCountries: number,
    nextCountry?: [number, string, Date],
    currentCountry?: [number, string]
}

interface WorldData extends LevelData {
    isWorldData: string, //TODO for detecting worlddata type at runtime
    loadingInfo: WorldLoadingInfo
}

export default WorldData
