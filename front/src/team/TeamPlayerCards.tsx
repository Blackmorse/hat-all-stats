import PlayerCardsTable from "../common/tables/player/PlayerCardsTable";
import TeamData from '../rest/models/leveldata/TeamData'
import TeamLevelDataProps from './TeamLevelDataProps'

class TeamPlayerCards extends PlayerCardsTable<TeamData, TeamLevelDataProps> {
}

export default TeamPlayerCards