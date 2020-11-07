import LevelDataProps from "../common/LevelDataProps";
import LeagueUnitData from "../rest/models/leveldata/LeagueUnitData";
import LevelRequest from "../rest/models/request/LevelRequest";
import LeagueUnitRequest from "../rest/models/request/LeagueUnitRequest";
import OverviewRequest from "../rest/models/request/OverviewRequest";

class LeagueUnitLevelDataProps extends LevelDataProps<LeagueUnitData> {  
    
    leagueId() {return this.levelData.leagueId}

    leagueUnitId() {return this.levelData.leagueUnitId}

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