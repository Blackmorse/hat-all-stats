import LeagueLevelDataProps from './LeagueLevelDataProps'
import LeagueData from '../rest/models/leveldata/LeagueData'

import TeamPowerRatingsTable from '../common/tables/team/TeamPowerRatingsTable'

class LeagueTeamPowerRatings extends TeamPowerRatingsTable<LeagueData, LeagueLevelDataProps> {
}

export default LeagueTeamPowerRatings