import TeamHatstats from '../common/tables/team/TeamHatstatsTable'
import LeagueLevelDataProps from './LeagueLevelDataProps'
import LeagueData from '../rest/models/leveldata/LeagueData'

class LeagueTeamHatstats extends TeamHatstats<LeagueData, LeagueLevelDataProps> {
}

export default LeagueTeamHatstats