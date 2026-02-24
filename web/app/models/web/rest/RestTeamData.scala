package models.web.rest

import models.web.rest.LevelData.Rounds
import play.api.libs.json.{Json, OWrites}
import service.leagueinfo.LoadingInfo
import zio.json.{DeriveJsonEncoder, JsonEncoder}

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
  implicit val writes: OWrites[RestTeamData] = Json.writes[RestTeamData]
  implicit val jsonEncoder: JsonEncoder[RestTeamData] = DeriveJsonEncoder.gen[RestTeamData]
}
