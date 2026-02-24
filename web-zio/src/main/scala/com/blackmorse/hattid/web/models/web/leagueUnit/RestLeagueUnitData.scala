package com.blackmorse.hattid.web.models.web.leagueUnit

import chpp.leaguedetails.models.LeagueDetails
import com.blackmorse.hattid.web.models.web.rest.CountryLevelData
import com.blackmorse.hattid.web.models.web.rest.LevelData.Rounds
import com.blackmorse.hattid.web.service.leagueinfo.{LeagueState, LoadingInfo}
import com.blackmorse.hattid.web.utils.{CurrencyUtils, Romans}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

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
  implicit val jsonEncoder: JsonEncoder[RestLeagueUnitData] = DeriveJsonEncoder.gen[RestLeagueUnitData]

  def apply(leagueDetails: LeagueDetails, leagueState: LeagueState, leagueUnitId: Long): RestLeagueUnitData =
    RestLeagueUnitData(
      leagueId = leagueDetails.leagueId,
      leagueName = leagueState.league.englishName,
      divisionLevel = leagueDetails.leagueLevel,
      divisionLevelName = Romans(leagueDetails.leagueLevel),
      leagueUnitId = leagueUnitId,
      leagueUnitName = leagueDetails.leagueLevelUnitName,
      teams = leagueDetails.teams.toSeq.map(team => (team.teamId, team.teamName)),
      seasonOffset = leagueState.league.seasonOffset,
      seasonRoundInfo = leagueState.seasonRoundInfo,
      currency = CurrencyUtils.currencyName(leagueState.league.country),
      currencyRate = CurrencyUtils.currencyRate(leagueState.league.country),
      loadingInfo = leagueState.loadingInfo,
      countries = leagueState.idToCountryName
    )
}
