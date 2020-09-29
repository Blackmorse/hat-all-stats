import { ModelTableProps } from "../common/ModelTable";
import DivisionLevelData from "../rest/models/DivisionLevelData";

class ModelTableDivisionLevelProps extends ModelTableProps<DivisionLevelData> {

    leagueId(): number{return this.levelData.leagueId;}

    divisionLevel(): number {return this.levelData.divisionLevel}
    seasonRoundInfo(): Array<[number, Array<number>]> {return this.levelData.seasonRoundInfo;}
}

export default ModelTableDivisionLevelProps