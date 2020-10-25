import PlayerRatingsTable from "../common/tables/player/PlayerRatingsTable";
import TeamData from '../rest/models/TeamData'
import ModelTableTeamProps from './ModelTableTeamProps'

class TeamPlayerRatings extends PlayerRatingsTable<TeamData, ModelTableTeamProps> {
}

export default TeamPlayerRatings