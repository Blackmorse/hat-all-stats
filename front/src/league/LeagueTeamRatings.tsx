import LeagueLevelDataProps from './LeagueLevelDataProps'
import LeagueData from '../rest/models/leveldata/LeagueData'
import TeamRatingsTable from '../common/tables/team/TeamRatingsTable'

class LeagueTeamRatings extends TeamRatingsTable<LeagueData, LeagueLevelDataProps> {
}

export default LeagueTeamRatings