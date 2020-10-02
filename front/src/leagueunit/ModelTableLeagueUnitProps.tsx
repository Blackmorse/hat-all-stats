import { ModelTableProps } from "../common/ModelTable";
import LeagueUnitData from "../rest/models/LeagueUnitData";

class ModelTableLeagueUnitProps extends ModelTableProps<LeagueUnitData> {    
    
    leagueId() {return this.levelData.leagueId}

    leagueUnitId() {return this.levelData.leagueUnitId}
}

export default ModelTableLeagueUnitProps