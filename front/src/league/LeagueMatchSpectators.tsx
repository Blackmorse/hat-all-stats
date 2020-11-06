import LeagueData from '../rest/models/leveldata/LeagueData'
import ModelTableLeagueProps from './ModelTableLeagueProps'
import MatchSpectatorsTable from '../common/tables/match/MatchSpectatorsTable';

class LeagueMatchSpectators extends MatchSpectatorsTable<LeagueData, ModelTableLeagueProps> {
}

export default LeagueMatchSpectators