import LevelDataProps from "../common/LevelDataProps";
import PlayerData from "../rest/models/leveldata/PlayerData";
import LevelRequest from "../rest/models/request/LevelRequest";
import OverviewRequest from "../rest/models/request/OverviewRequest";
import PlayerRequest from "../rest/models/request/PlayerRequest";

class PlayerLevelDataProps extends LevelDataProps<PlayerData> {
    leagueId(): number {
        return this.levelData.leagueId
    }

    createLevelRequest(): LevelRequest {
        const playerRequest: PlayerRequest = {
            type: 'PlayerRequest',
            playerId: this.levelData.playerId
        }
        return playerRequest
    }

    createOverviewRequest(): OverviewRequest {
        throw new Error("Mpt supported");
    }
}

export default PlayerLevelDataProps
