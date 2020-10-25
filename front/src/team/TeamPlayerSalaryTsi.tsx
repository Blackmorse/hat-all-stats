import PlayerSalaryTsiTable from "../common/tables/player/PlayerSalaryTsiTable";
import TeamData from '../rest/models/TeamData'
import ModelTableTeamProps from './ModelTableTeamProps'

class TeamPlayerSalaryTsi extends PlayerSalaryTsiTable<TeamData, ModelTableTeamProps>{
}

export default TeamPlayerSalaryTsi