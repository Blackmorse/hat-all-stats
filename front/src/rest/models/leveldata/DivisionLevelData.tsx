import CountryLevelData from './CountryLevelData'

interface DivisionLevelData extends CountryLevelData {
    divisionLevel: number,
    divisionLevelName: string,
    leagueUnitsNumber: number,
} 

export default DivisionLevelData