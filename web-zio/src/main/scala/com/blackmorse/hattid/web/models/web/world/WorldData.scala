package com.blackmorse.hattid.web.models.web.world

import zio.json.{DeriveJsonEncoder, JsonEncoder}
import com.blackmorse.hattid.web.models.web.rest.LevelData.Rounds
import com.blackmorse.hattid.web.models.web.rest.LevelData

case class WorldData(countries: Seq[(Int, String)],
                     seasonOffset: Int,
                     seasonRoundInfo: Seq[(Int, Rounds)],
                     currency: String,
                     currencyRate: Double,
                     loadingInfo: Option[WorldLoadingInfo],
                     isWorldData: String /*TODO for detecting type at TS*/) extends LevelData

object WorldData {
  implicit val jsonEncoder: JsonEncoder[WorldData] = DeriveJsonEncoder.gen[WorldData]
}
