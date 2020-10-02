import { ModelTableProps } from "../common/ModelTable";
import DivisionLevelData from "../rest/models/DivisionLevelData";

class ModelTableDivisionLevelProps extends ModelTableProps<DivisionLevelData> {

    leagueId(): number{return this.levelData.leagueId;}

    divisionLevel(): number {return this.levelData.divisionLevel}
}

export default ModelTableDivisionLevelProps