import PlayerGoalsGamesTable from "../common/tables/player/PlayerGoalsGamesTable";
import TeamData from '../rest/models/leveldata/TeamData'
import TeamLevelDataProps from './TeamLevelDataProps'

class TeamPlayerGoalGames extends PlayerGoalsGamesTable<TeamData, TeamLevelDataProps>{
}

export default TeamPlayerGoalGames