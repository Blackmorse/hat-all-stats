import CountryLevelData from "./CountryLevelData";

interface PlayerData extends CountryLevelData {
    playerId: number 
    firstName: string
    lastName: string
    divisionLevel: number
    divisionLevelName: string
    leagueUnitId: number
    leagueUnitName: string
    teamId: number
    teamName: string
}

export default PlayerData
