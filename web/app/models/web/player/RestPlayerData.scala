package models.web.player

import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import models.web.translations.LanguageTranslations
import play.api.libs.json.{Json, OWrites}
import service.leagueinfo.LoadingInfo
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class RestPlayerData(playerId: Long,
                          firstName: String,
                          lastName: String,
                          leagueId: Int,
                          leagueName: String,
                          divisionLevel: Int,
                          divisionLevelName: String,
                          leagueUnitId: Int,
                          leagueUnitName: String,
                          teamId: Long,
                          teamName: String,
                          seasonOffset: Int,
                          seasonRoundInfo: Seq[(Int, Rounds)],
                          currency: String,
                          currencyRate: Double,
                          countries: Seq[(Int, String)],
                          loadingInfo: LoadingInfo,
                          translations: Seq[(String, LanguageTranslations)]) extends CountryLevelData

object RestPlayerData {
  implicit val writes: OWrites[RestPlayerData] = Json.writes[RestPlayerData]
  implicit val jsonEncoder: JsonEncoder[RestPlayerData] = DeriveJsonEncoder.gen[RestPlayerData]
}