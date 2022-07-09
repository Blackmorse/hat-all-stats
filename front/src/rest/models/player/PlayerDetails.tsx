

interface PlayerDetails {
  playerId: number,
  firstName: string,
  lastName: string,
  salary: number,
  tsi: number,
  historyList: Array<PlayerHistory>
}

export interface PlayerHistory {
  playerId: number,
  firstName: string,
  lastName: string,
  age: number,
  tsi: number,
  rating: number,
  ratingEndOfMatch: number,
  matchType: string,
  role: String,
  playedMinutes: number,
  injuryLevel: number,
  salary: number,
  yellowCards: number,
  redCards: number
}

export default PlayerDetails
