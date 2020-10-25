import PlayerCardsTable from "../common/tables/player/PlayerCardsTable";
import TeamData from '../rest/models/TeamData'
import ModelTableTeamProps from './ModelTableTeamProps'

class TeamPlayerCards extends PlayerCardsTable<TeamData, ModelTableTeamProps> {
}

export default TeamPlayerCards