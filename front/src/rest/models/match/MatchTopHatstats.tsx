import { LeagueId } from '../../../common/overview/OverviewPage'
import TeamSortingKey from '../team/TeamSortingKey'

interface MatchTopHatstats extends LeagueId {
    homeTeam: TeamSortingKey,
    awayTeam: TeamSortingKey,
    homeHatstats: number,
    homeGoals: number,
    homeLoddarStats: number,
    awayHatstats: number,
    awayGoals: number,
    matchId: number,
    awayLoddarStats: number
}

export default MatchTopHatstats