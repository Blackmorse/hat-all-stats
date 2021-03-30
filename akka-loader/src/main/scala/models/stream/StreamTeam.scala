package models.stream

case class StreamTeam(leagueUnit: LeagueUnit,
                      userId: Long,
                      id: Long,
                      name: String,
                      position: Int,
                      points: Int,
                      diff: Int,
                      scored: Int) {
  def samePosition(other: StreamTeam): Boolean = {
    position == other.position && points == other.points && diff == other.diff && scored == other.scored
  }
}
