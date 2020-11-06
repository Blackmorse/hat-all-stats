import LeagueData from '../rest/models/leveldata/LeagueData'
import LeagueLevelDataProps from './LeagueLevelDataProps'
import MatchSpectatorsTable from '../common/tables/match/MatchSpectatorsTable';

class LeagueMatchSpectators extends MatchSpectatorsTable<LeagueData, LeagueLevelDataProps> {
}

export default LeagueMatchSpectators