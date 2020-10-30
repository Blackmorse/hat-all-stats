import LeagueData from '../rest/models/LeagueData'
import ModelTableLeagueProps from './ModelTableLeagueProps'
import MatchTopHatstatsTable from "../common/tables/match/MatchTopHatstatsTable";

class LeagueMatchTopHatstats extends MatchTopHatstatsTable<LeagueData, ModelTableLeagueProps> {
}

export default LeagueMatchTopHatstats