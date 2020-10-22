import ModelTableDivisionLevelProps from './ModelTableDivisionLevelProps'
import DivisionLevelData from '../rest/models/DivisionLevelData';
import PlayerGoalsGamesTable from '../common/tables/PlayerGoalsGamesTable'
import StatisticsParameters from '../rest/StatisticsParameters';
import RestTableData from '../rest/RestTableData';
import PlayerGoalGames from '../rest/models/player/PlayerGoalsGames';
import DivisionLevelRequest from '../rest/models/request/DivisionLevelRequest';
import { getPlayerGoalsGames } from '../rest/Client';

class DivisionLevelPlayerGoalGames extends PlayerGoalsGamesTable<DivisionLevelData, ModelTableDivisionLevelProps> {
    fetchEntities(tableProps: ModelTableDivisionLevelProps, 
            statisticsParameters: StatisticsParameters, 
            callback: (restTableData: RestTableData<PlayerGoalGames>) => void,
            onError: () => void): void {
            
        const divisionLevelRequest: DivisionLevelRequest = {
            type: 'DivisionLevelRequest',
            leagueId: tableProps.leagueId(),
            divisionLevel: tableProps.divisionLevel()
        } 

        getPlayerGoalsGames(divisionLevelRequest, statisticsParameters, callback, onError)
    }
}    

export default DivisionLevelPlayerGoalGames