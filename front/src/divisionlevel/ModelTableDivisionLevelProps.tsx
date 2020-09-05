import { ModelTableProps } from "../common/ModelTable";
import DivisionLevelData from "../rest/models/DivisionLevelData";

class ModelTableDivisionLevelProps implements ModelTableProps<DivisionLevelData> {
    private divisionLevelData: DivisionLevelData

    constructor(divisionLevelData: DivisionLevelData) {
        this.divisionLevelData = divisionLevelData
    }
    
    leagueId(): number{return this.divisionLevelData.leagueId;}
    currentSeason(): number {return this.divisionLevelData.currentSeason;}
    seasons(): number[] {return this.divisionLevelData.seasons;}
    currentRound(): number  {return this.divisionLevelData.currentRound;}
    rounds(): number[] {return this.divisionLevelData.rounds;}

    divisionLevel(): number {return this.divisionLevelData.divisionLevel}
}

export default ModelTableDivisionLevelProps