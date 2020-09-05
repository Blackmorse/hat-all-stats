import LevelData from './LevelData'

interface DivisionLevelData extends LevelData{
    leagueId: number,
    leagueName: string,
    divisionLevel: number,
    divisionLevelName: string,
    leagueUnitsNumber: number,
    currentRound: number,
    rounds: Array<number>,
    currentSeason: number,
    seasons: Array<number>
} 

export default DivisionLevelData