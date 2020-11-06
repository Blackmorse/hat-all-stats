import LeagueData from '../rest/models/leveldata/LeagueData'
import ModelTableLeagueProps from './ModelTableLeagueProps'
import PlayerRatingsTable from "../common/tables/player/PlayerRatingsTable";

class LeaguePlayerRatings extends PlayerRatingsTable<LeagueData, ModelTableLeagueProps> {
}

export default LeaguePlayerRatings