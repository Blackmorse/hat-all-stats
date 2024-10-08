package loadergraph.playerevents

import models.clickhouse.PlayerEventsModelCH

case class PlayerEventsAccumulator(var season: Int,
                                   var round: Int,
                                   var playerId: Long,
                                   var yellowCards: Int = 0,
                                   var redCards: Int = 0,
                                   var goals: Int = 0,
                                   var injury: Int = 0,
                                   var leftFieldMinute: Int = - 1,
                                   var startingLineup: Boolean = false) {
  def build: PlayerEventsModelCH =
    PlayerEventsModelCH(season = season,
      round = round,
      playerId = playerId,
      yellowCards = yellowCards,
      redCards = redCards,
      goals = goals,
      injury = injury,
      leftFieldMinute = leftFieldMinute,
      startingLineup = if (startingLineup) 1 else 0)
}
