package com.blackmorse.hattid.web.models.web.divisionlevel

import com.blackmorse.hattid.web.models.web.rest.LevelData.Rounds
import com.blackmorse.hattid.web.models.web.rest.CountryLevelData
import com.blackmorse.hattid.web.service.leagueinfo.LoadingInfo
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class RestDivisionLevelData(leagueId: Int,
                                 leagueName: String,
                                 divisionLevel: Int,
                                 divisionLevelName: String,
                                 leagueUnitsNumber: Int,
                                 seasonOffset: Int,
                                 seasonRoundInfo: Seq[(Int, Rounds)],
                                 currency: String,
                                 currencyRate: Double,
                                 loadingInfo: LoadingInfo,
                                 countries: Seq[(Int, String)]) extends CountryLevelData

object RestDivisionLevelData {
  implicit val jsonEncoder: JsonEncoder[RestDivisionLevelData] = DeriveJsonEncoder.gen[RestDivisionLevelData]
}