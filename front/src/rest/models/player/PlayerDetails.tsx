interface PlayerDetails {
  playerId: number,
  firstName: string,
  lastName: string,
  currentPlayerCharacteristics: CurrentPlayerCharacteristics,
  nativeLeagueId: number,
  playerLeagueUnitHistory: Array<PlayerLeagueUnitEntry>,
  avatar: Array<AvatarPart>,
  playerSeasonStats: Array<PlayerSeasonStats>
}

interface AvatarPart {
    url: string
    x: number
    y: number
}

interface CurrentPlayerCharacteristics {
  position: string,
  salary: number,
  tsi: number,
  age: number,
  form: number,
  injuryLevel: number,
  experience: number,
  leaderShip: number,
  speciality: number
}


interface PlayerLeagueUnitEntry{
  season: number,
  round: number,
  fromLeagueId: number,
  fromLeagueUnitId: number,
  fromLeagueUnitName: string,
  fromTeamId: number,
  fromTeamName: string,
  toLeagueId: number,
  toLeagueUnitId: number,
  toLeagueUnitName: string,
  toTeamId: number,
  toTeamName: string,
  tsi: number,
  salary: number,
  age: number
}

interface PlayerSeasonStats {
  season: number,
  leagueGoals: number,
  cupGoals: number,
  allGoals: number,
  yellowCards: number,
  redCards: number,
  matches: number,
  playedMinutes: number
}

export default PlayerDetails
