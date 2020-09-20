import { ModelTableProps } from "../common/ModelTable";
import LeagueUnitData from "../rest/models/LeagueUnitData";

class ModelTableLeagueUnitProps implements ModelTableProps<LeagueUnitData> {
    private leagueUnitData: LeagueUnitData
    
    constructor(leagueUnitData: LeagueUnitData) {
        this.leagueUnitData = leagueUnitData
    }
    
    leagueId() {return this.leagueUnitData.leagueId}
    currentSeason() {return this.leagueUnitData.currentSeason}
    seasons() {return this.leagueUnitData.seasons} 
    currentRound() {return this.leagueUnitData.currentRound}
    rounds() {return this.leagueUnitData.rounds}

    leagueUnitId() {return this.leagueUnitData.leagueUnitId}
}

export default ModelTableLeagueUnitProps