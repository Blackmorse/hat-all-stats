package models.clickhouse

case class PlayerEventsModelCH(season: Int,
                               round: Int,
                               playerId: Long,
                               yellowCards: Int,
                               redCards: Int,
                               goals: Int,
                               injury: Int,
                               leftFieldMinute: Int)
