package models.clickhouse

import spray.json.{JsNumber, JsObject, JsValue, JsonFormat}

case class PlayerEventsModelCH(season: Int,
                               round: Int,
                               playerId: Long,
                               yellowCards: Int,
                               redCards: Int,
                               goals: Int,
                               injury: Int,
                               leftFieldMinute: Int,
                               startingLineup: Int)

object PlayerEventsModelCH {
  implicit val format: JsonFormat[PlayerEventsModelCH] = new JsonFormat[PlayerEventsModelCH] {
    override def read(json: JsValue): PlayerEventsModelCH = null

    override def write(obj: PlayerEventsModelCH): JsValue = {
      JsObject(
        ("season", JsNumber(obj.season)),
        ("round", JsNumber(obj.round)),
        ("player_id", JsNumber(obj.playerId)),
        ("yellow_cards", JsNumber(obj.yellowCards)),
        ("red_cards", JsNumber(obj.redCards)),
        ("goals", JsNumber(obj.goals)),
        ("injury", JsNumber(obj.injury)),
        ("left_minute", JsNumber(obj.leftFieldMinute)),
        ("starting_lineup", JsNumber(obj.startingLineup))
      )
    }
  }
}
