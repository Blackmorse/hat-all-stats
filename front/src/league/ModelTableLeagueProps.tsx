import { ModelTableProps } from "../common/ModelTable";
import LeagueData from "../rest/models/LeagueData";

class ModelTableLeagueProps implements ModelTableProps<LeagueData> {
    private leagueData: LeagueData;

    constructor(ld: LeagueData) {
        this.leagueData = ld;
    }

    leagueId() {return this.leagueData.leagueId}
    currentSeason()  {return this.leagueData.currentSeason}
    seasons() {return this.leagueData.seasons}
    currentRound() {return this.leagueData.currentRound}
    rounds() {return this.leagueData.rounds}
}

export default ModelTableLeagueProps