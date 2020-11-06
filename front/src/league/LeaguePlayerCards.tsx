import PlayerCardsTable from "../common/tables/player/PlayerCardsTable";
import LeagueData from '../rest/models/leveldata/LeagueData'
import LeagueLevelDataProps from './LeagueLevelDataProps'

class LeaguePlayerCards extends PlayerCardsTable<LeagueData, LeagueLevelDataProps> {
}

export default LeaguePlayerCards