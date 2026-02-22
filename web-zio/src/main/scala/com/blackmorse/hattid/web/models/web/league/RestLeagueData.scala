package com.blackmorse.hattid.web.models.web.league

import com.blackmorse.hattid.web.models.web.rest.LevelData.Rounds
import com.blackmorse.hattid.web.models.web.rest.CountryLevelData
import com.blackmorse.hattid.web.service.leagueinfo.LoadingInfo
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class RestLeagueData(leagueId: Int,
                          leagueName: String,
                          divisionLevels: Seq[String],
                          seasonOffset: Int,
                          seasonRoundInfo: Seq[(Int, Rounds)],
                          currency: String,
                          currencyRate: Double,
                          loadingInfo: LoadingInfo,
                          countries: Seq[(Int, String)]) extends CountryLevelData

object RestLeagueData {
  implicit val jsonEncoder: JsonEncoder[RestLeagueData] = DeriveJsonEncoder.gen[RestLeagueData]
}
