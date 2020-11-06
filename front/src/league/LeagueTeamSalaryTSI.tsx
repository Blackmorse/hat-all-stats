import LeagueLevelDataProps from './LeagueLevelDataProps'
import LeagueData from '../rest/models/leveldata/LeagueData'
import TeamSalaryTSITable from '../common/tables/team/TeamSalaryTSITable'

class LeagueTeamSalaryTSI extends TeamSalaryTSITable<LeagueData, LeagueLevelDataProps> {
}

export default LeagueTeamSalaryTSI