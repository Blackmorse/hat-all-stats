import { ModelTableProps } from "../common/ModelTable";
import TeamData from "../rest/models/TeamData";

class ModelTableTeamProps extends ModelTableProps<TeamData> {
    leagueId(): number {
        return this.levelData.leagueId
    }

    teamId(): number {
        return this.levelData.teamId
    }
}

export default ModelTableTeamProps