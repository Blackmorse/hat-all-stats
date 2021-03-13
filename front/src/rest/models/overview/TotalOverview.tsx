import NumberOverview from './NumberOverview'
import FormationsOverview from './FormationsOverview'
import AveragesOverview from './AveragesOverview'
import TeamStatOverview from './TeamStatOverview'
import PlayerStatOverview from './PlayerStatOverview'
import MatchTopHatstats from '../match/MatchTopHatstats'
import MatchAttendanceOverview from './MatchAttendanceOverview'

interface TotalOverview {
    numberOverview: NumberOverview,
    formations: Array<FormationsOverview>,
    averageOverview: AveragesOverview,
    surprisingMatches: Array<MatchTopHatstats>,
    topHatstatsTeams: Array<TeamStatOverview>,
    topSalaryTeams: Array<TeamStatOverview>,
    topMatches: Array<MatchTopHatstats>,
    topSalaryPlayers: Array<PlayerStatOverview>,
    topRatingPlayers: Array<PlayerStatOverview>,
    topMatchAttendance: Array<MatchAttendanceOverview>,
    topTeamVictories: Array<TeamStatOverview>,
    topSeasonScorers: Array<PlayerStatOverview>
}

export default TotalOverview