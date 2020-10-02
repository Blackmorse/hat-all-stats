import { ModelTableProps } from "../common/ModelTable";
import TeamData from "../rest/models/TeamData";

class ModelTableTeamProps extends ModelTableProps<TeamData> {
    leagueId(): number {
        return this.levelData.leagueId
    }
}

export default ModelTableTeamProps