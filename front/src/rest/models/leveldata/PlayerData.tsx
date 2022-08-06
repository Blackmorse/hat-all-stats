import CountryLevelData from "./CountryLevelData";

interface PlayerData extends CountryLevelData {
    playerId: number 
    firstName: string
    lastName: string
    divisionLevel: number
    divisionLevelName: string
    leagueUnitId: number
    leagueUnitName: string
    teamId: number
    teamName: string
    translations: Array<[string, LanguageTranslations]>
}

export interface LanguageTranslations {
    skillTranslations: Array<TranslationLevel>
    specialities: Array<TranslationLevel>
}

export interface TranslationLevel {
    level: number
    translation: string
}

export default PlayerData
