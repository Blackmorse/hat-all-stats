import PlayerInjuriesTable from "../common/tables/player/PlayerInjuriesTable";
import TeamData from '../rest/models/TeamData'
import ModelTableTeamProps from './ModelTableTeamProps'

class TeamPlayerInjuries extends PlayerInjuriesTable<TeamData, ModelTableTeamProps> {
}

export default TeamPlayerInjuries