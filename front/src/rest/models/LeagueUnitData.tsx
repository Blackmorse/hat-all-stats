import LevelData from './LevelData'

interface LeagueUnitData extends LevelData {
    leagueId: number,
    leagueName: string,
    divisionLevel: number,
    divisionLevelName: string,
    leagueUnitId: number,
    leagueUnitName: string,
    currentRound: number,
    rounds: Array<number>,
    currentSeason: number,
    seasons: Array<number>,
    teams: Array<[number, string]>
}

export default LeagueUnitData