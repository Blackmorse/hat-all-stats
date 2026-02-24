package com.blackmorse.hattid.web.models.web.player

import com.blackmorse.hattid.web.models.web.rest.LevelData.Rounds
import com.blackmorse.hattid.web.models.web.translations.LanguageTranslations
import zio.json.{DeriveJsonEncoder, JsonEncoder}
import com.blackmorse.hattid.web.service.leagueinfo.LoadingInfo
import com.blackmorse.hattid.web.models.web.rest.CountryLevelData

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
  implicit val jsonEncoder: JsonEncoder[RestPlayerData] = DeriveJsonEncoder.gen[RestPlayerData]
}