import LevelDataProps from "../common/LevelDataProps";
import WorldData from "../rest/models/leveldata/WorldData";
import LevelRequest from "../rest/models/request/LevelRequest";

class WorldLevelDataProps extends LevelDataProps<WorldData> {
    leagueId(): number {
        throw new Error("Method not implemented.");
    }
    createLevelRequest(): LevelRequest {
        throw new Error("Method not implemented.");
    }

}

export default WorldLevelDataProps