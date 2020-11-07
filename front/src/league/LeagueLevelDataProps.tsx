import LevelDataProps from "../common/LevelDataProps";
import LeagueData from "../rest/models/leveldata/LeagueData";
import LevelRequest from "../rest/models/request/LevelRequest";
import LeagueRequest from "../rest/models/request/LeagueRequest";
import OverviewRequest from "../rest/models/request/OverviewRequest";

class LeagueLevelDataProps extends LevelDataProps<LeagueData> {
    createLevelRequest(): LevelRequest {
        const leagueRequest: LeagueRequest = {
            type: 'LeagueRequest', 
            leagueId: this.leagueId()
        }
        return leagueRequest
    }
    leagueId() {return this.levelData.leagueId}

    createOverviewRequest(): OverviewRequest {
        return {
            season: super.currentSeason(),
            round: super.currentRound(),
            leagueId: this.leagueId()
        }
    }
}

export default LeagueLevelDataProps