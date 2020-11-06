import { ModelTableProps } from "../common/ModelTable";
import LeagueData from "../rest/models/leveldata/LeagueData";
import LevelRequest from "../rest/models/request/LevelRequest";
import LeagueRequest from "../rest/models/request/LeagueRequest";

class ModelTableLeagueProps extends ModelTableProps<LeagueData> {
    createLevelRequest(): LevelRequest {
        const leagueRequest: LeagueRequest = {
            type: 'LeagueRequest', 
            leagueId: this.leagueId()
        }
        return leagueRequest
    }
    leagueId() {return this.levelData.leagueId}
}

export default ModelTableLeagueProps