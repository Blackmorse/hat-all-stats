import LevelDataProps from "../common/LevelDataProps";
import LeagueUnitData from "../rest/models/leveldata/LeagueUnitData";
import LevelRequest from "../rest/models/request/LevelRequest";
import LeagueUnitRequest from "../rest/models/request/LeagueUnitRequest";

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
}

export default LeagueUnitLevelDataProps