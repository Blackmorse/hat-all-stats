import TeamHatstats from '../common/tables/TeamHatstats'
import ModelTableDivisionLevelProps from './ModelTableDivisionLevelProps'
import StatisticsParameters from '../rest/StatisticsParameters'
import { getTeamRatings } from '../rest/Client';
import DivisionLevelRequest from '../rest/models/request/DivisionLevelRequest';
import RestTableData from '../rest/RestTableData'
import TeamRating from '../rest/models/TeamRating';
import DivisionLevelData from '../rest/models/DivisionLevelData';

class DivisionLevelTeamHatstats extends TeamHatstats<DivisionLevelData, ModelTableDivisionLevelProps> {
    fetchEntities(tableProps: ModelTableDivisionLevelProps, 
            statisticsParameters: StatisticsParameters, 
            callback: (restTableData: RestTableData<TeamRating>) => void,
            onError: () => void): void {
                
        const divisionLevelRequest: DivisionLevelRequest = {
            type: 'DivisionLevelRequest',
            leagueId: tableProps.leagueId(),
            divisionLevel: tableProps.divisionLevel()
        } 

        getTeamRatings(divisionLevelRequest, statisticsParameters, callback, onError)
    }

}

export default DivisionLevelTeamHatstats