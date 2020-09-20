import TeamHatstats from '../common/tables/TeamHatstats'
import ModelTableLeagueUnitProps from './ModelTableLeagueUnitProps'
import { ModelTableProps } from '../common/ModelTable'
import StatisticsParameters from '../rest/StatisticsParameters'
import RestTableData from '../rest/RestTableData'
import TeamRating from '../rest/models/TeamRating'
import { getTeamRatings } from '../rest/Client';
import LeagueUnitRequest from '../rest/models/request/LeagueUnitRequest'


class LeagueUnitTeamHatstats extends TeamHatstats<ModelTableLeagueUnitProps> {
    fetchEntities(tableProps: ModelTableLeagueUnitProps, 
            statisticsParameters: StatisticsParameters,     
            callback: (restTableData: RestTableData<TeamRating>) => void): void {
        
        const leagueUnitRequest: LeagueUnitRequest = {
            type: 'LeagueUnitRequest',
            leagueUnitId: tableProps.leagueUnitId()
        }

        getTeamRatings(leagueUnitRequest, statisticsParameters, callback)
    }

}

export default LeagueUnitTeamHatstats