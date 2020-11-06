import LeagueData from '../rest/models/leveldata/LeagueData'
import LeagueLevelDataProps from './LeagueLevelDataProps'
import PlayerRatingsTable from "../common/tables/player/PlayerRatingsTable";

class LeaguePlayerRatings extends PlayerRatingsTable<LeagueData, LeagueLevelDataProps> {
}

export default LeaguePlayerRatings