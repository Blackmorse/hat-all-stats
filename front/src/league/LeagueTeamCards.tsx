import LeagueLevelDataProps from './LeagueLevelDataProps'
import LeagueData from '../rest/models/leveldata/LeagueData'
import TeamCardsTable from '../common/tables/team/TeamCardsTable'

class LeagueTeamCards extends TeamCardsTable<LeagueData, LeagueLevelDataProps> {
}

export default LeagueTeamCards