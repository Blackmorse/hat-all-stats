import CountryLevelDataProps from "../common/CountryLevelDataProps";
import PlayerData, {LanguageTranslations, TranslationLevel} from "../rest/models/leveldata/PlayerData";
import LevelRequest from "../rest/models/request/LevelRequest";
import OverviewRequest from "../rest/models/request/OverviewRequest";
import PlayerRequest from "../rest/models/request/PlayerRequest";

class PlayerLevelDataProps extends CountryLevelDataProps {
    playerData: PlayerData

    constructor(playerData: PlayerData) {
        super(playerData)
        this.playerData = playerData
    }

    playerId(): number { return this.playerData.playerId }

    divisionLevel(): number { return this.playerData.divisionLevel }

    divisionLevelName(): string { return this.playerData.divisionLevelName }

    teamId(): number { return this.playerData.teamId }

    teamName(): string { return this.playerData.teamName }

    leagueUnitId(): number { return this.playerData.leagueUnitId }

    leagueUnitName(): string { return this.playerData.leagueUnitName }

    firstName(): string { return this.playerData.firstName }

    lastName(): string { return this.playerData.lastName }

    createLevelRequest(): LevelRequest {
        const playerRequest: PlayerRequest = {
            type: 'PlayerRequest',
            playerId: this.playerData.playerId
        }
        return playerRequest
    }

    createOverviewRequest(): OverviewRequest {
        throw new Error("Not supported");
    }

    private getTranslation(lang: string, level: number, entryFunc: (languageTranslations: LanguageTranslations) => Array<TranslationLevel>): string {
        const translations = this.playerData.translations.find(it => lang === it[0])
        if (translations === undefined) {
            return level.toString()
        }
        const translation = entryFunc(translations[1]).find(levelTranslation => levelTranslation.level === level)
        if (translation === undefined) return level.toString()
        return translation.translation + ' (' + level + ')'
    }

    skillLevelTranslation(lang: string, level: number): string {
        return this.getTranslation(lang, level, lt => lt.skillTranslations)
//        let translations = this.playerData.translations.find(it => lang === it[0])
//        if (translations === undefined) {
//            return level.toString()
//        }
//        let translation = translations[1].skillTranslations.find(levelTranslation => levelTranslation.level === level)
//        if (translation === undefined) return level.toString()
//        return translation.translation + ' (' + level + ')'
    }

    specialityTranslation(lang: string, id: number): string {
        return this.getTranslation(lang, id, lt => lt.specialities)
//        let translations = this.playerData.translations.find(it => lang === it[0])
//        if (translations === undefined) {
//            return id.toString()
//        }
//        let translation = translations[1].specialities.find(levelTranslation => levelTranslation.level === id)
//        if (translation === undefined) return id.toString()
//        return translation.translation + ' (' + id + ')'
    }
}

export default PlayerLevelDataProps
