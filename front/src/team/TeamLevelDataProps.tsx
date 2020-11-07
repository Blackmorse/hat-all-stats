import LevelDataProps from "../common/LevelDataProps";
import TeamData from "../rest/models/leveldata/TeamData";
import LevelRequest from "../rest/models/request/LevelRequest";
import TeamRequest from "../rest/models/request/TeamRequest";
import OverviewRequest from "../rest/models/request/OverviewRequest";

class TeamLevelDataProps extends LevelDataProps<TeamData> {
    
    leagueId(): number {
        return this.levelData.leagueId
    }

    teamId(): number {
        return this.levelData.teamId
    }

    createLevelRequest(): LevelRequest {
        const teamRequest: TeamRequest = {
            type: 'TeamRequest',
            teamId: this.teamId()
        }
        return teamRequest
    }

    createOverviewRequest(): OverviewRequest {
        throw new Error("Not supported")
    }
}

export default TeamLevelDataProps