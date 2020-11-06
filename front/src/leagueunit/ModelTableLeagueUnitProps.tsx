import { ModelTableProps } from "../common/ModelTable";
import LeagueUnitData from "../rest/models/leveldata/LeagueUnitData";
import LevelRequest from "../rest/models/request/LevelRequest";
import LeagueUnitRequest from "../rest/models/request/LeagueUnitRequest";

class ModelTableLeagueUnitProps extends ModelTableProps<LeagueUnitData> {  
    
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

export default ModelTableLeagueUnitProps