import type TeamData from "../rest/models/leveldata/TeamData";
import type LevelRequest from "../rest/models/request/LevelRequest";
import type TeamRequest from "../rest/models/request/TeamRequest";
import type OverviewRequest from "../rest/models/request/OverviewRequest";
import CountryLevelDataProps from "../common/CountryLevelDataProps";

class TeamLevelDataProps extends CountryLevelDataProps {
    teamData: TeamData

    constructor(teamData: TeamData) {
        super(teamData)
        this.teamData = teamData
    }
    
    teamId(): number {
        return this.teamData.teamId
    }

    teamName(): string { return this.teamData.teamName }

    leagueUnitName(): string { return this.teamData.leagueUnitName }

    divisionLevel(): number { return this.teamData.divisionLevel }

    divisionLevelName(): string { return this.teamData.divisionLevelName }

    leagueUnitId(): number { return this.teamData.leagueUnitId }

    foundedDate(): number { return this.teamData.foundedDate }

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
