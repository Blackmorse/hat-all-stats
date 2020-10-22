import PlayerGoalGamesTable from '../common/tables/PlayerGoalsGamesTable'
import LeagueData from '../rest/models/LeagueData'
import ModelTableLeagueProps from './ModelTableLeagueProps'
import StatisticsParameters from '../rest/StatisticsParameters'
import RestTableData from '../rest/RestTableData'
import PlayerGoalGames from '../rest/models/player/PlayerGoalsGames'
import LeagueRequest from '../rest/models/request/LeagueRequest'
import { getPlayerGoalsGames } from '../rest/Client';

class LeaguePlayerGoalGames extends PlayerGoalGamesTable<LeagueData, ModelTableLeagueProps> {
    fetchEntities(tableProps: ModelTableLeagueProps, 
            statisticsParameters: StatisticsParameters,
            callback: (restTableData: RestTableData<PlayerGoalGames>) => void,
            onError: () => void) {
        const leagueRequest: LeagueRequest = {type: 'LeagueRequest', leagueId: tableProps.leagueId()}
        getPlayerGoalsGames(leagueRequest, statisticsParameters, callback, onError)
    }
}

export default LeaguePlayerGoalGames