package com.blackmorse.hattid.web.models.web.rest

import zio.json.{DeriveJsonEncoder, JsonEncoder}
import com.blackmorse.hattid.web.service.leagueinfo.LoadingInfo
import com.blackmorse.hattid.web.models.web.rest.LevelData.Rounds

case class RestTeamData(leagueId: Int,
                        leagueName: String,
                        divisionLevel: Int,
                        divisionLevelName: String,
                        leagueUnitId: Long,
                        leagueUnitName: String,
                        teamId: Long,
                        teamName: String,
                        foundedDate: Long,
                        seasonOffset: Int,
                        seasonRoundInfo: Seq[(Int, Rounds)],
                        currency: String,
                        currencyRate: Double,
                        loadingInfo: LoadingInfo,
                        countries: Seq[(Int, String)]) extends CountryLevelData

object RestTeamData {
  implicit val jsonEncoder: JsonEncoder[RestTeamData] = DeriveJsonEncoder.gen[RestTeamData]
}
