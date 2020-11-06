import PlayerRatingsTable from "../common/tables/player/PlayerRatingsTable";
import TeamData from '../rest/models/leveldata/TeamData'
import ModelTableTeamProps from './ModelTableTeamProps'

class TeamPlayerRatings extends PlayerRatingsTable<TeamData, ModelTableTeamProps> {
}

export default TeamPlayerRatings