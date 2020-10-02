import { ModelTableProps } from "../common/ModelTable";
import LeagueData from "../rest/models/LeagueData";

class ModelTableLeagueProps extends ModelTableProps<LeagueData> {
    leagueId() {return this.levelData.leagueId}
}

export default ModelTableLeagueProps