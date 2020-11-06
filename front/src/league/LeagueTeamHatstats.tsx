import TeamHatstats from '../common/tables/team/TeamHatstatsTable'
import ModelTableLeagueProps from './ModelTableLeagueProps'
import LeagueData from '../rest/models/leveldata/LeagueData'

class LeagueTeamHatstats extends TeamHatstats<LeagueData, ModelTableLeagueProps> {
}

export default LeagueTeamHatstats