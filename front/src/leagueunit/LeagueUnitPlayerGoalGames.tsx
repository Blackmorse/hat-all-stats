import LeagueUnitLevelDataProps from './LeagueUnitLevelDataProps'
import LeagueUnitData from '../rest/models/leveldata/LeagueUnitData'
import PlayerGoalGamesTable from '../common/tables/player/PlayerGoalsGamesTable'

class LeagueUnitPlayerGoalGames extends PlayerGoalGamesTable<LeagueUnitData, LeagueUnitLevelDataProps> {
}

export default LeagueUnitPlayerGoalGames