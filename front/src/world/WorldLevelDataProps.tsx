import LevelDataProps from "../common/LevelDataProps";
import WorldData from "../rest/models/leveldata/WorldData";
import LevelRequest from "../rest/models/request/LevelRequest";
import OverviewRequest from "../rest/models/request/OverviewRequest";

class WorldLevelDataProps extends LevelDataProps<WorldData> {
    leagueId(): number {
        throw new Error("Method not implemented.");
    }
    createLevelRequest(): LevelRequest {
        throw new Error("Method not implemented.");
    }

    createOverviewRequest(): OverviewRequest {
        return {
            season: super.currentSeason(),
            round: super.currentRound()
        }
    }
}

export default WorldLevelDataProps