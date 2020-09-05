import TeamHatstats from '../common/tables/TeamHatstats'
import ModelTableDivisionLevelProps from './ModelTableDivisionLevelProps'
import StatisticsParameters from '../rest/StatisticsParameters'
import { getTeamRatings } from '../rest/Client';
import DivisionLevelRequest from '../rest/models/request/DivisionLevelRequest';

class DivisionLevelTeamHatstats extends TeamHatstats<ModelTableDivisionLevelProps> {
    fetchEntities(tableProps: ModelTableDivisionLevelProps, 
            statisticsParameters: StatisticsParameters, 
            callback: (restTableData: import("../rest/RestTableData").default<import("../rest/models/TeamRating").default>) => void): void {
        const divisionLevelRequest: DivisionLevelRequest = {
            type: 'DivisionLevelRequest',
            leagueId: tableProps.leagueId(),
            divisionLevel: tableProps.divisionLevel()
        } 

        getTeamRatings(divisionLevelRequest, statisticsParameters, callback)
    }

}

export default DivisionLevelTeamHatstats