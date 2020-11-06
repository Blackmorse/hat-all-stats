import LeagueLevelDataProps from './LeagueLevelDataProps'
import LeagueData from '../rest/models/leveldata/LeagueData'
import TeamStreakTrophiesTable from '../common/tables/team/TeamStreakTrophiesTable'

class LeagueTeamStreakTrophies extends TeamStreakTrophiesTable<LeagueData, LeagueLevelDataProps> {
}

export default LeagueTeamStreakTrophies