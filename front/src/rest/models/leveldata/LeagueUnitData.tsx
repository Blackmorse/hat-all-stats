import LevelData from './LevelData'

interface LeagueUnitData extends LevelData {
    leagueId: number,
    leagueName: string,
    divisionLevel: number,
    divisionLevelName: string,
    leagueUnitId: number,
    leagueUnitName: string
    teams: Array<[number, string]>
}

export default LeagueUnitData