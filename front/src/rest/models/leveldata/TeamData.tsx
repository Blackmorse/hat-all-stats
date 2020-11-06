import LevelData from './LevelData'

interface TeamData extends LevelData {
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