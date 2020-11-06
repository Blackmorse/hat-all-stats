import LevelData from './LevelData'

interface DivisionLevelData extends LevelData {
    leagueId: number,
    leagueName: string,
    divisionLevel: number,
    divisionLevelName: string,
    leagueUnitsNumber: number,
} 

export default DivisionLevelData