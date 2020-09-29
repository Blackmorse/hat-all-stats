import ModelTableDivisionLevelProps from './ModelTableDivisionLevelProps'
import LeagueUnits from '../common/tables/LeagueUnits'
import StatisticsParameters from '../rest/StatisticsParameters'
import RestTableData from '../rest/RestTableData'
import LeagueUnitRating from '../rest/models/LeagueUnitRating'
import { getLeagueUnits } from '../rest/Client'
import DivisionLevelRequest from '../rest/models/request/DivisionLevelRequest'
import DivisionLevelData from '../rest/models/DivisionLevelData'

class DivisionLevelLeagueUnits extends LeagueUnits<DivisionLevelData> {
    fetchEntities(tableProps: ModelTableDivisionLevelProps, 
            statisticsParameters: StatisticsParameters, 
            callback: (restTableData: RestTableData<LeagueUnitRating>) => void): void {
        const divisionLevelRequest: DivisionLevelRequest = {
            type: 'DivisionLevelRequest',
            leagueId: tableProps.leagueId(),
            divisionLevel: tableProps.divisionLevel()
        }
        getLeagueUnits(divisionLevelRequest, statisticsParameters, callback)
    }
}

export default DivisionLevelLeagueUnits