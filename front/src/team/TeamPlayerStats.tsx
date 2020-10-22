import PlayerStats from '../common/tables/PlayerStats'
import TeamData from '../rest/models/TeamData'
import ModelTableTeamProps from './ModelTableTeamProps'

class TeamPlayerStats extends PlayerStats<TeamData, ModelTableTeamProps> {
}

export default TeamPlayerStats