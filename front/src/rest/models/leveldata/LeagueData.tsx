import CountryLevelData from './CountryLevelData'

interface LeagueData extends CountryLevelData {
    leagueId: number,
    leagueName: string,
    divisionLevels: Array<string>
}

export default LeagueData