import LeagueData from "../rest/models/leveldata/LeagueData";
import LevelRequest from "../rest/models/request/LevelRequest";
import LeagueRequest from "../rest/models/request/LeagueRequest";
import OverviewRequest from "../rest/models/request/OverviewRequest";
import CountryLevelDataProps from "../common/CountryLevelDataProps";

class LeagueLevelDataProps extends CountryLevelDataProps {
    leagueData: LeagueData

    constructor(leagueData: LeagueData) {
        super(leagueData)
        this.leagueData = leagueData
    }

    createLevelRequest(): LevelRequest {
        const leagueRequest: LeagueRequest = {
            type: 'LeagueRequest', 
            leagueId: this.leagueId()
        }
        return leagueRequest
    }

    divisionLevels() { return this.leagueData.divisionLevels }

    createOverviewRequest(): OverviewRequest {
        return {
            season: super.currentSeason(),
            round: super.currentRound(),
            leagueId: this.leagueId()
        }
    }
}

export default LeagueLevelDataProps
