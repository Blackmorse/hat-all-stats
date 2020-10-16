import TeamHatstats from '../common/tables/TeamHatstats'
import ModelTableLeagueProps from './ModelTableLeagueProps'
import StatisticsParameters from '../rest/StatisticsParameters'
import RestTableData from '../rest/RestTableData'
import TeamRating from '../rest/models/TeamRating'
import { getTeamRatings } from '../rest/Client';
import LeagueRequest from '../rest/models/request/LeagueRequest'
import LeagueData from '../rest/models/LeagueData'

class LeagueTeamHatstats extends TeamHatstats<LeagueData, ModelTableLeagueProps> {
    fetchEntities(tableProps: ModelTableLeagueProps, statisticsParameters: StatisticsParameters, 
            callback: (restTableData: RestTableData<TeamRating>) => void,
            onError: () => void): void {
        const leagueRequest: LeagueRequest = {type: 'LeagueRequest', leagueId: tableProps.leagueId()}
        getTeamRatings(leagueRequest, statisticsParameters, callback, onError)
    }
}

export default LeagueTeamHatstats