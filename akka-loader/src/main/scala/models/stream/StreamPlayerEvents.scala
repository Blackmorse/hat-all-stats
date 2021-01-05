package models.stream

case class StreamPlayerEvents(season: Int,
                              round: Int,
                              playerId: Long,
                              yellowCards: Int,
                              redCards: Int,
                              goals: Int,
                              injury: Int,
                              leftFieldMinute: Int)
