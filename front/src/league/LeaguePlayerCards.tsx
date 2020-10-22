import PlayerCardsTable from "../common/tables/PlayerCardsTable";
import LeagueData from '../rest/models/LeagueData'
import ModelTableLeagueProps from './ModelTableLeagueProps'
import StatisticsParameters from "../rest/StatisticsParameters";
import RestTableData from "../rest/RestTableData";
import PlayerCards from "../rest/models/player/PlayerCards";
import LeagueRequest from '../rest/models/request/LeagueRequest'
import { getPlayerCards } from '../rest/Client';

class LeaguePlayerCards extends PlayerCardsTable<LeagueData, ModelTableLeagueProps> {
    fetchEntities(tableProps: ModelTableLeagueProps, 
            statisticsParameters: StatisticsParameters,
            callback: (restTableData: RestTableData<PlayerCards>) => void,
            onError: () => void) {
        const leagueRequest: LeagueRequest = {type: 'LeagueRequest', leagueId: tableProps.leagueId()}
        getPlayerCards(leagueRequest, statisticsParameters, callback, onError)
    }
}

export default LeaguePlayerCards