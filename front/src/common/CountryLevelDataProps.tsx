import CountryLevelData, {LoadingInfo} from "../rest/models/leveldata/CountryLevelData";
import LevelDataProps from "./LevelDataProps";


abstract class CountryLevelDataProps extends LevelDataProps {
    levelData: CountryLevelData

    constructor(levelData: CountryLevelData) {
        super(levelData)
        this.levelData = levelData
    }

    leagueId(): number { return this.levelData.leagueId }
    leagueName(): string { return this.levelData.leagueName }
    loadingInfo(): LoadingInfo { return this.levelData.loadingInfo }
}

export default CountryLevelDataProps
