import PlayerInjuriesTable from "../common/tables/player/PlayerInjuriesTable";
import LeagueData from '../rest/models/LeagueData'
import ModelTableLeagueProps from './ModelTableLeagueProps'

class LeaguePlayerInjuries extends PlayerInjuriesTable<LeagueData, ModelTableLeagueProps> {
}

export default LeaguePlayerInjuries