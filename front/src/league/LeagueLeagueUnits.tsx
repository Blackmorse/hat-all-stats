import LeagueUnits from '../common/tables/LeagueUnits'
import StatisticsParameters from '../rest/StatisticsParameters'
import LeagueRequest from '../rest/models/request/LeagueRequest'
import { getLeagueUnits } from '../rest/Client'
import ModelTableLeagueProps from './ModelTableLeagueProps'
import LeagueData from '../rest/models/LeagueData'
import RestTableData from '../rest/RestTableData'
import LeagueUnitRating from '../rest/models/LeagueUnitRating'

class LeagueLeagueUnits extends LeagueUnits<LeagueData, ModelTableLeagueProps> {
    fetchEntities(tableProps: ModelTableLeagueProps, statisticsParameters: StatisticsParameters, callback: (restTableData: RestTableData<LeagueUnitRating>) => void): void {
        const leagueRequest: LeagueRequest = {type: 'LeagueRequest', leagueId: tableProps.leagueId()}
        getLeagueUnits(leagueRequest, statisticsParameters, callback)
    }
}

export default LeagueLeagueUnits