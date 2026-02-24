package com.blackmorse.hattid.web.models.web.league

import zio.json.{DeriveJsonDecoder, JsonDecoder}
import java.util.Date

case class LeagueTime(leagueId: Int, time: Date)

object LeagueTime {
  implicit val jsonDecoder: JsonDecoder[LeagueTime] = DeriveJsonDecoder.gen[LeagueTime]
  implicit val dateDecoder: JsonDecoder[Date] = JsonDecoder[Long].map(millis => new Date(millis))
}
