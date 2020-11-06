import PlayerSalaryTsiTable from "../common/tables/player/PlayerSalaryTsiTable";
import TeamData from '../rest/models/leveldata/TeamData'
import TeamLevelDataProps from './TeamLevelDataProps'

class TeamPlayerSalaryTsi extends PlayerSalaryTsiTable<TeamData, TeamLevelDataProps>{
}

export default TeamPlayerSalaryTsi