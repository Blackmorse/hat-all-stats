import LevelDataProps from "../common/LevelDataProps";
import DivisionLevelData from "../rest/models/leveldata/DivisionLevelData";
import LevelRequest from "../rest/models/request/LevelRequest";
import DivisionLevelRequest from "../rest/models/request/DivisionLevelRequest";
import OverviewRequest from "../rest/models/request/OverviewRequest";

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