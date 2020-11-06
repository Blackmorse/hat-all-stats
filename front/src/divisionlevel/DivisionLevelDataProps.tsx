import LevelDataProps from "../common/LevelDataProps";
import DivisionLevelData from "../rest/models/leveldata/DivisionLevelData";
import LevelRequest from "../rest/models/request/LevelRequest";
import DivisionLevelRequest from "../rest/models/request/DivisionLevelRequest";

class DivisionLevelDataProps extends LevelDataProps<DivisionLevelData> {
    leagueId(): number{return this.levelData.leagueId;}

    divisionLevel(): number {return this.levelData.divisionLevel}

    createLevelRequest(): LevelRequest {
        const divisionLevelRequest: DivisionLevelRequest = {
            type: 'DivisionLevelRequest',
            leagueId: this.leagueId(),
            divisionLevel: this.divisionLevel()
        } 
        return divisionLevelRequest
    }
}

export default DivisionLevelDataProps