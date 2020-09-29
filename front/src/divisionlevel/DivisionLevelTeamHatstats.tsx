import TeamHatstats from '../common/tables/TeamHatstats'
import ModelTableDivisionLevelProps from './ModelTableDivisionLevelProps'
import StatisticsParameters from '../rest/StatisticsParameters'
import { getTeamRatings } from '../rest/Client';
import DivisionLevelRequest from '../rest/models/request/DivisionLevelRequest';
import RestTableData from '../rest/RestTableData'
import TeamRating from '../rest/models/TeamRating';

class DivisionLevelTeamHatstats extends TeamHatstats<ModelTableDivisionLevelProps> {
    fetchEntities(tableProps: ModelTableDivisionLevelProps, 
            statisticsParameters: StatisticsParameters, 
            callback: (restTableData: RestTableData<TeamRating>) => void): void {
                
        const divisionLevelRequest: DivisionLevelRequest = {
            type: 'DivisionLevelRequest',
            leagueId: tableProps.leagueId(),
            divisionLevel: tableProps.divisionLevel()
        } 

        getTeamRatings(divisionLevelRequest, statisticsParameters, callback)
    }

}

export default DivisionLevelTeamHatstats