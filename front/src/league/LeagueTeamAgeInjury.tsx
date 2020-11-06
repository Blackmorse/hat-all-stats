import LeagueLevelDataProps from './LeagueLevelDataProps'
import LeagueData from '../rest/models/leveldata/LeagueData'
import TeamAgeInjuryTable from '../common/tables/team/TeamAgeInjuryTable'

class LeagueTeamAgeInjury extends TeamAgeInjuryTable<LeagueData, LeagueLevelDataProps> {
}

export default LeagueTeamAgeInjury