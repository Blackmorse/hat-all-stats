package com.blackmorse.hattid.web.models.web.world

import java.util.Date
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class WorldLoadingInfo(proceedCountries: Int,
                            nextCountry: Option[(Int, String, Date)],
                            currentCountry: Option[(Int, String)])

object WorldLoadingInfo {
  implicit val jsonEncoder: JsonEncoder[WorldLoadingInfo] = DeriveJsonEncoder.gen[WorldLoadingInfo]
  implicit val dateEncoder: JsonEncoder[Date] = JsonEncoder[Long].contramap(_.getTime)
}

