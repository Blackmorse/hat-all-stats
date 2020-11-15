import CountryLevelData from './CountryLevelData'

interface TeamData extends CountryLevelData {
    leagueId: number,
    leagueName: string,
    divisionLevel: number,
    divisionLevelName: string,
    leagueUnitId: number,
    leagueUnitName: string,
    teamId: number,
    teamName: string
}

export default TeamData