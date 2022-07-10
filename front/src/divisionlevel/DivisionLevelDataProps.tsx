import DivisionLevelData from "../rest/models/leveldata/DivisionLevelData";
import LevelRequest from "../rest/models/request/LevelRequest";
import DivisionLevelRequest from "../rest/models/request/DivisionLevelRequest";
import OverviewRequest from "../rest/models/request/OverviewRequest";
import CountryLevelDataProps from "../common/CountryLevelDataProps";

class DivisionLevelDataProps extends CountryLevelDataProps {
    divisionLevelData: DivisionLevelData

    constructor(divisionLevelData: DivisionLevelData) {
        super(divisionLevelData)
        this.divisionLevelData = divisionLevelData
    }

    divisionLevel(): number {return this.divisionLevelData.divisionLevel}

    divisionLevelName(): string { return this.divisionLevelData.divisionLevelName }

    leagueUnitsNumber(): number { return this.divisionLevelData.leagueUnitsNumber }

    createLevelRequest(): LevelRequest {
        const divisionLevelRequest: DivisionLevelRequest = {
            type: 'DivisionLevelRequest',
            leagueId: this.leagueId(),
            divisionLevel: this.divisionLevel()
        } 
        return divisionLevelRequest
    }

    createOverviewRequest(): OverviewRequest {
        return {
            season: super.currentSeason(),
            round: super.currentRound(),
            leagueId: this.leagueId(),
            divisionLevel: this.divisionLevel()
        }
    }
}

export default DivisionLevelDataProps
