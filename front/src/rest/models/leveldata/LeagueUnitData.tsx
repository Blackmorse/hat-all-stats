import CountryLevelData from './CountryLevelData'

interface LeagueUnitData extends CountryLevelData {
    leagueId: number,
    leagueName: string,
    divisionLevel: number,
    divisionLevelName: string,
    leagueUnitId: number,
    leagueUnitName: string
    teams: Array<[number, string]>
}

export default LeagueUnitData