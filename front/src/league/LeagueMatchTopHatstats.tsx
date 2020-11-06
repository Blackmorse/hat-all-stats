import LeagueData from '../rest/models/leveldata/LeagueData'
import LeagueLevelDataProps from './LeagueLevelDataProps'
import MatchTopHatstatsTable from "../common/tables/match/MatchTopHatstatsTable";

class LeagueMatchTopHatstats extends MatchTopHatstatsTable<LeagueData, LeagueLevelDataProps> {
}

export default LeagueMatchTopHatstats