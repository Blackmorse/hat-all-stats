import ModelTableDivisionLevelProps from './ModelTableDivisionLevelProps'
import DivisionLevelData from '../rest/models/DivisionLevelData';
import PlayerCardsTable from '../common/tables/PlayerCardsTable'
import StatisticsParameters from '../rest/StatisticsParameters';
import RestTableData from '../rest/RestTableData';
import PlayerCards from '../rest/models/player/PlayerCards';
import DivisionLevelRequest from '../rest/models/request/DivisionLevelRequest';
import { getPlayerCards } from '../rest/Client';

class DivisionLevelPlayerCards extends PlayerCardsTable<DivisionLevelData, ModelTableDivisionLevelProps> {
    fetchEntities(tableProps: ModelTableDivisionLevelProps, 
            statisticsParameters: StatisticsParameters, 
            callback: (restTableData: RestTableData<PlayerCards>) => void,
            onError: () => void): void {
            
        const divisionLevelRequest: DivisionLevelRequest = {
            type: 'DivisionLevelRequest',
            leagueId: tableProps.leagueId(),
            divisionLevel: tableProps.divisionLevel()
        } 

        getPlayerCards(divisionLevelRequest, statisticsParameters, callback, onError)
    }
}    

export default DivisionLevelPlayerCards