import LeagueUnitData from "../rest/models/leveldata/LeagueUnitData";
import LevelRequest from "../rest/models/request/LevelRequest";
import LeagueUnitRequest from "../rest/models/request/LeagueUnitRequest";
import OverviewRequest from "../rest/models/request/OverviewRequest";
import CountryLevelDataProps from "../common/CountryLevelDataProps";

class LeagueUnitLevelDataProps extends CountryLevelDataProps {  
    leagueUnitData: LeagueUnitData

    constructor(leagueUnitData: LeagueUnitData) {
        super(leagueUnitData)
        this.leagueUnitData = leagueUnitData
    }
    
    leagueUnitId() {return this.leagueUnitData.leagueUnitId}

    leagueUnitName() { return this.leagueUnitData.leagueUnitName }

    divisionLevel() { return this.leagueUnitData.divisionLevel }

    divisionLevelName() { return this.leagueUnitData.divisionLevelName }

    teams() { return this.leagueUnitData.teams }

    createLevelRequest(): LevelRequest {
        const leagueUnitRequest: LeagueUnitRequest = {
            type: 'LeagueUnitRequest',
            leagueUnitId: this.leagueUnitId()
        }

        return leagueUnitRequest
    }

    createOverviewRequest(): OverviewRequest {
        throw new Error("Not supported")
    }
}

export default LeagueUnitLevelDataProps
