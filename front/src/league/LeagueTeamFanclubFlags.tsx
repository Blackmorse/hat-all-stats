import LeagueLevelDataProps from './LeagueLevelDataProps'
import LeagueData from '../rest/models/leveldata/LeagueData'
import TeamFanclubFlagsTable from '../common/tables/team/TeamFanclubFlagsTable'

class LeagueTeamFanclubFlags extends TeamFanclubFlagsTable<LeagueData, LeagueLevelDataProps> {
}

export default LeagueTeamFanclubFlags