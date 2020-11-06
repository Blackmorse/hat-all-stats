import PlayerInjuriesTable from "../common/tables/player/PlayerInjuriesTable";
import LeagueData from '../rest/models/leveldata/LeagueData'
import LeagueLevelDataProps from './LeagueLevelDataProps'

class LeaguePlayerInjuries extends PlayerInjuriesTable<LeagueData, LeagueLevelDataProps> {
}

export default LeaguePlayerInjuries