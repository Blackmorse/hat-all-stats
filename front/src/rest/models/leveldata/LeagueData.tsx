import LevelData from './LevelData'

interface LeagueData extends LevelData{
    leagueId: number,
    leagueName: string,
    divisionLevels: Array<string>
}

export default LeagueData