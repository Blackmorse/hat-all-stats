import { ModelTableProps } from "../common/ModelTable";
import WorldData from "../rest/models/leveldata/WorldData";
import LevelRequest from "../rest/models/request/LevelRequest";

class ModelTableWorldProps extends ModelTableProps<WorldData> {
    leagueId(): number {
        throw new Error("Method not implemented.");
    }
    createLevelRequest(): LevelRequest {
        throw new Error("Method not implemented.");
    }

}

export default ModelTableWorldProps