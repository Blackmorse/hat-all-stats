import LeagueLevelDataProps from './LeagueLevelDataProps'
import LeagueData from '../rest/models/leveldata/LeagueData'
import TeamGoalPointsTable from '../common/tables/team/TeamGoalPointsTable'

class LeagueTeamGoalPoints extends TeamGoalPointsTable<LeagueData, LeagueLevelDataProps> {
}

export default LeagueTeamGoalPoints