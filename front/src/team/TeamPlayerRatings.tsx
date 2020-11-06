import PlayerRatingsTable from "../common/tables/player/PlayerRatingsTable";
import TeamData from '../rest/models/leveldata/TeamData'
import TeamLevelDataProps from './TeamLevelDataProps'

class TeamPlayerRatings extends PlayerRatingsTable<TeamData, TeamLevelDataProps> {
}

export default TeamPlayerRatings