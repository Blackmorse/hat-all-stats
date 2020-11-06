import ModelTableLeagueProps from './ModelTableLeagueProps'
import LeagueData from '../rest/models/leveldata/LeagueData'
import TeamGoalPointsTable from '../common/tables/team/TeamGoalPointsTable'

class LeagueTeamGoalPoints extends TeamGoalPointsTable<LeagueData, ModelTableLeagueProps> {
}

export default LeagueTeamGoalPoints