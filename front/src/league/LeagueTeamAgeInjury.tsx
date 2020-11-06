import ModelTableLeagueProps from './ModelTableLeagueProps'
import LeagueData from '../rest/models/leveldata/LeagueData'
import TeamAgeInjuryTable from '../common/tables/team/TeamAgeInjuryTable'

class LeagueTeamAgeInjury extends TeamAgeInjuryTable<LeagueData, ModelTableLeagueProps> {
}

export default LeagueTeamAgeInjury