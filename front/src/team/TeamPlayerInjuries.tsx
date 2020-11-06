import PlayerInjuriesTable from "../common/tables/player/PlayerInjuriesTable";
import TeamData from '../rest/models/leveldata/TeamData'
import TeamLevelDataProps from './TeamLevelDataProps'

class TeamPlayerInjuries extends PlayerInjuriesTable<TeamData, TeamLevelDataProps> {
}

export default TeamPlayerInjuries