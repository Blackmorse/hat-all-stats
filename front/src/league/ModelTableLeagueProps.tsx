import { ModelTableProps } from "../common/ModelTable";
import LeagueData from "../rest/models/LeagueData";

class ModelTableLeagueProps extends ModelTableProps<LeagueData> {
    seasonRoundInfo(): Array<[number, Array<number>]> {return this.levelData.seasonRoundInfo}

    leagueId() {return this.levelData.leagueId}
}

export default ModelTableLeagueProps