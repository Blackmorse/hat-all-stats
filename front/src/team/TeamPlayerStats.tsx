import PlayerStats from '../common/tables/PlayerStats'
import TeamData from '../rest/models/TeamData'
import ModelTableTeamProps from './ModelTableTeamProps'
import StatisticsParameters from '../rest/StatisticsParameters'
import RestTableData from '../rest/RestTableData'
import PlayerStat from "../rest/models/PlayerStat"
import TeamRequest from '../rest/models/request/TeamRequest'
import { getPlayerStats } from '../rest/Client'

class TeamPlayerStats extends PlayerStats<TeamData, ModelTableTeamProps> {
    fetchEntities(tableProps: ModelTableTeamProps, 
            statisticsParameters: StatisticsParameters, 
            callback: (restTableData: RestTableData<PlayerStat>) => void): void {
        
        const teamRequest: TeamRequest = {
            type: 'TeamRequest',
            teamId: tableProps.teamId()
        }

        getPlayerStats(teamRequest, statisticsParameters, callback)
    }
}

export default TeamPlayerStats