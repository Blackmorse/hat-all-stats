import LevelDataProps from "../common/LevelDataProps";
import TeamData from "../rest/models/leveldata/TeamData";
import LevelRequest from "../rest/models/request/LevelRequest";
import TeamRequest from "../rest/models/request/TeamRequest";

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
}

export default TeamLevelDataProps