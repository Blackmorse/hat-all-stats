import PlayerGoalsGamesTable from "../common/tables/PlayerGoalsGamesTable";
import TeamData from '../rest/models/TeamData'
import ModelTableTeamProps from './ModelTableTeamProps'

class TeamPlayerGoalGames extends PlayerGoalsGamesTable<TeamData, ModelTableTeamProps>{
}

export default TeamPlayerGoalGames