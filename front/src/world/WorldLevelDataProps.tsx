import LevelDataProps from "../common/LevelDataProps";
import WorldData, { type WorldLoadingInfo } from "../rest/models/leveldata/WorldData";
import type LevelRequest from "../rest/models/request/LevelRequest";
import type OverviewRequest from "../rest/models/request/OverviewRequest";
import type WorldRequest from '../rest/models/request/WorldRequest'

class WorldLevelDataProps extends LevelDataProps {
    worldData: WorldData

    constructor(worldData: WorldData) {
        super(worldData)
        this.worldData = worldData
    }
    
    loadingInfo(): WorldLoadingInfo { return this.worldData.loadingInfo }

    leagueId(): number {
        throw new Error("Method not implemented.");
    }

    createLevelRequest(): LevelRequest {
        const request: WorldRequest = {
            type: 'WorldRequest'
        }
        return request
    }

    createOverviewRequest(): OverviewRequest {
        return {
            season: super.currentSeason(),
            round: super.currentRound()
        }
    }
}

export default WorldLevelDataProps
