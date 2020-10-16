import TeamHatstats from '../common/tables/TeamHatstats'
import ModelTableLeagueUnitProps from './ModelTableLeagueUnitProps'
import StatisticsParameters from '../rest/StatisticsParameters'
import RestTableData from '../rest/RestTableData'
import TeamRating from '../rest/models/TeamRating'
import { getTeamRatings } from '../rest/Client';
import LeagueUnitRequest from '../rest/models/request/LeagueUnitRequest'
import LeagueUnitData from '../rest/models/LeagueUnitData'


class LeagueUnitTeamHatstats extends TeamHatstats<LeagueUnitData, ModelTableLeagueUnitProps> {
    fetchEntities(tableProps: ModelTableLeagueUnitProps, 
            statisticsParameters: StatisticsParameters,     
            callback: (restTableData: RestTableData<TeamRating>) => void,
            onError: () => void): void {
        
        const leagueUnitRequest: LeagueUnitRequest = {
            type: 'LeagueUnitRequest',
            leagueUnitId: tableProps.leagueUnitId()
        }

        getTeamRatings(leagueUnitRequest, statisticsParameters, callback, onError)
    }

}

export default LeagueUnitTeamHatstats