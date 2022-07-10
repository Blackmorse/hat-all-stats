import CountryLevelDataProps from "../common/CountryLevelDataProps";
import PlayerData from "../rest/models/leveldata/PlayerData";
import LevelRequest from "../rest/models/request/LevelRequest";
import OverviewRequest from "../rest/models/request/OverviewRequest";
import PlayerRequest from "../rest/models/request/PlayerRequest";

class PlayerLevelDataProps extends CountryLevelDataProps {
    playerData: PlayerData

    constructor(playerData: PlayerData) {
        super(playerData)
        this.playerData = playerData
    }

    playerId(): number { return this.playerData.playerId }

    divisionLevel(): number { return this.playerData.divisionLevel }

    divisionLevelName(): string { return this.playerData.divisionLevelName }

    teamId(): number { return this.playerData.teamId }

    teamName(): string { return this.playerData.teamName }

    leagueUnitId(): number { return this.playerData.leagueUnitId }

    leagueUnitName(): string { return this.playerData.leagueUnitName }

    firstName(): string { return this.playerData.firstName }

    lastName(): string { return this.playerData.lastName }

    createLevelRequest(): LevelRequest {
        const playerRequest: PlayerRequest = {
            type: 'PlayerRequest',
            playerId: this.playerData.playerId
        }
        return playerRequest
    }

    createOverviewRequest(): OverviewRequest {
        throw new Error("Mpt supported");
    }
}

export default PlayerLevelDataProps
