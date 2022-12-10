package models.web.leagueUnit

import chpp.leaguedetails.models.LeagueDetails
import chpp.worlddetails.models.League
import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import play.api.libs.json.{Json, OWrites}
import service.leagueinfo.{LeagueInfoService, LoadingInfo}
import utils.{CurrencyUtils, Romans}

case class RestLeagueUnitData(leagueId: Int,
                              leagueName: String,
                              divisionLevel: Int,
                              divisionLevelName: String,
                              leagueUnitId: Long,
                              leagueUnitName: String,
                              teams: Seq[(Long, String)],
                              seasonOffset: Int,
                              seasonRoundInfo: Seq[(Int, Rounds)],
                              currency: String,
                              currencyRate: Double,
                              loadingInfo: LoadingInfo,
                              countries: Seq[(Int, String)]) extends CountryLevelData

object RestLeagueUnitData {
  implicit val writes: OWrites[RestLeagueUnitData] = Json.writes[RestLeagueUnitData]

  def apply(leagueDetails: LeagueDetails, league: League, leagueUnitId: Long, leagueInfoService: LeagueInfoService): RestLeagueUnitData =
    RestLeagueUnitData(
      leagueId = leagueDetails.leagueId,
      leagueName = league.englishName,
      divisionLevel = leagueDetails.leagueLevel,
      divisionLevelName = Romans(leagueDetails.leagueLevel),
      leagueUnitId = leagueUnitId,
      leagueUnitName = leagueDetails.leagueLevelUnitName,
      teams = leagueDetails.teams.toSeq.map(team => (team.teamId, team.teamName)),
      seasonOffset = league.seasonOffset,
      seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueDetails.leagueId),
      currency = CurrencyUtils.currencyName(league.country),
      currencyRate = CurrencyUtils.currencyRate(league.country),
      loadingInfo = leagueInfoService.leagueInfo(leagueDetails.leagueId).loadingInfo,
      countries = leagueInfoService.idToStringCountryMap
    )
}
