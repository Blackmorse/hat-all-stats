package controllers

import java.util.Date

import com.blackmorse.hattrick.common.CommonData.higherLeagueMap
import com.blackmorse.hattrick.model.enums.SearchType
import databases.RestClickhouseDAO
import databases.requests.matchdetails.{MatchSpectatorsRequest, MatchSurprisingRequest, MatchTopHatstatsRequest, TeamHatstatsRequest}
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player._
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamdetails.{TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import hattrick.Hattrick
import io.swagger.annotations.Api
import javax.inject.Inject
import models.web.rest.{CountryLevelData, LevelData}
import models.web.rest.LevelData.Rounds
import models.web.{RestStatisticsParameters, RestTableData, Round, StatsType}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.ControllerComponents
import service.LeagueUnitCalculatorService
import service.leagueinfo.{LeagueInfoService, LoadingInfo, Scheduled}
import utils.{LeagueNameParser, Romans}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
                              loadingInfo: LoadingInfo) extends CountryLevelData

object RestLeagueUnitData {
  implicit val writes = Json.writes[RestLeagueUnitData]
}

@Api(produces = "application/json")
class RestLeagueUnitController @Inject() (val controllerComponents: ControllerComponents,
                                          val leagueInfoService: LeagueInfoService,
                                          val hattrick: Hattrick,
                                          implicit val restClickhouseDAO: RestClickhouseDAO,
                                          val leagueUnitCalculatorService: LeagueUnitCalculatorService) extends RestController {
  case class LongWrapper(id: Long)
  implicit val writes = Json.writes[LongWrapper]

  def leagueUnitIdByName(leagueUnitName: String, leagueId: Int) = Action.async{implicit request =>
    Future(findLeagueUnitIdByName(leagueUnitName, leagueId))
      .map(id => Ok(Json.toJson(LongWrapper(id))))
  }

  private def findLeagueUnitIdByName(leagueUnitName: String, leagueId: Int) = {
    if(leagueUnitName == "I.1") {
      higherLeagueMap.get(leagueId).getLeagueUnitId
    } else {
      if(leagueId == 1) { //Sweden
        val (division, number) = LeagueNameParser.getLeagueUnitNumberByName(leagueUnitName)
        val actualDivision = Romans(Romans(division) - 1)
        val actualNumber = if (division == "II" || division == "III") {
          ('a' + number - 1).toChar.toString
        } else {
          "." + number.toString
        }
        hattrick.api.search()
          .searchLeagueId(leagueId).searchString(actualDivision + actualNumber).searchType(SearchType.SERIES)
          .execute()
          .getSearchResults.get(0).getResultId
      } else {
        hattrick.api.search()
          .searchLeagueId(leagueId).searchString(leagueUnitName).searchType(SearchType.SERIES)
          .execute()
          .getSearchResults.get(0).getResultId
      }
    }
  }

  private def leagueUnitDataFromId(leagueUnitId: Long): Future[RestLeagueUnitData] =
    Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())
    .map(leagueDetails => {
      val league = leagueInfoService.leagueInfo(leagueDetails.getLeagueId).league

      RestLeagueUnitData(leagueId = leagueDetails.getLeagueId,
        leagueName = league.getEnglishName,
        divisionLevel = leagueDetails.getLeagueLevel,
        divisionLevelName = Romans(leagueDetails.getLeagueLevel),
        leagueUnitId = leagueUnitId,
        leagueUnitName = leagueDetails.getLeagueLevelUnitName,
        teams = leagueDetails.getTeams.asScala.map(team => (team.getTeamId.toLong, team.getTeamName)),
        seasonOffset = league.getSeasonOffset,
        seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueDetails.getLeagueId),
        currency = if (league.getCountry.getCurrencyName == null) "$" else league.getCountry.getCurrencyName,
        currencyRate = if (league.getCountry.getCurrencyRate == null) 10.0d else league.getCountry.getCurrencyRate,
        loadingInfo = leagueInfoService.leagueInfo(leagueDetails.getLeagueId).loadingInfo)
    }
      )

  def getLeagueUnitData(leagueUnitId: Long) = Action.async {implicit request =>
    leagueUnitDataFromId(leagueUnitId)
      .map(rlud =>  Ok(Json.toJson(rlud)))
  }

  def stats[T](chRequest: ClickhouseStatisticsRequest[T],
               leagueUnitId: Long,
               restStatisticsParameters: RestStatisticsParameters)
              (implicit writes: Writes[T]) = Action.async{ implicit request =>
    leagueUnitDataFromId(leagueUnitId).flatMap(leagueUnitData =>
      chRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitId)),
        restStatisticsParameters
      ) .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
    )
  }

  def teamHatstats(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamHatstatsRequest, leagueUnitId, restStatisticsParameters)

  def playerGoalGames(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerGamesGoalsRequest, leagueUnitId, restStatisticsParameters)

  def playerCards(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerCardsRequest, leagueUnitId, restStatisticsParameters)

  def playerTsiSalary(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerSalaryTSIRequest, leagueUnitId, restStatisticsParameters)

  def playerRatings(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerRatingsRequest, leagueUnitId, restStatisticsParameters)

  def playerInjuries(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerInjuryRequest, leagueUnitId, restStatisticsParameters)

  def teamSalaryTsi(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamSalaryTSIRequest, leagueUnitId, restStatisticsParameters)

  def teamCards(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamCardsRequest, leagueUnitId, restStatisticsParameters)

  def teamRatings(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamRatingsRequest, leagueUnitId, restStatisticsParameters)

  def teamAgeInjuries(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamAgeInjuryRequest, leagueUnitId, restStatisticsParameters)

  def teamPowerRatings(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamPowerRatingsRequest, leagueUnitId, restStatisticsParameters)

  def teamFanclubFlags(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamFanclubFlagsRequest, leagueUnitId, restStatisticsParameters)

  def teamStreakTrophies(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamStreakTrophiesRequest, leagueUnitId, restStatisticsParameters)

  def topMatches(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(MatchTopHatstatsRequest, leagueUnitId, restStatisticsParameters)

  def surprisingMatches(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(MatchSurprisingRequest, leagueUnitId, restStatisticsParameters)

  def matchSpectators(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(MatchSpectatorsRequest, leagueUnitId, restStatisticsParameters)

  def teamPositions(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) = Action.async{implicit request =>
    Future{
      val leagueDetails = hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute()

      val offsettedSeason = leagueInfoService.getRelativeSeasonFromAbsolute(restStatisticsParameters.season, leagueDetails.getLeagueId)

      val leagueFixture = hattrick.api.leagueFixtures().season(offsettedSeason).leagueLevelUnitId(leagueUnitId).execute()

      val round = restStatisticsParameters.statsType.asInstanceOf[Round].round

      val teams = leagueUnitCalculatorService.calculate(leagueFixture, Some(round),
        restStatisticsParameters.sortBy, restStatisticsParameters.sortingDirection)

      Ok(Json.toJson(RestTableData(teams, true)))
    }
  }

  def promotions(leagueUnitId: Long) = Action.async{ implicit request =>
    leagueUnitDataFromId(leagueUnitId).flatMap(leagueUnitData =>
      PromotionsRequest.execute(OrderingKeyPath(
          leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitData.leagueUnitId)), leagueInfoService.leagueInfo.currentSeason(leagueUnitData.leagueId)))
      .map(PromotionWithType.convert)
      .map(result => Ok(Json.toJson(result)))
  }

  def dreamTeam(season: Int, leagueUnitId: Long, sortBy: String, statsType: StatsType) = Action.async{ implicit request =>
    leagueUnitDataFromId(leagueUnitId).flatMap(leagueUnitData =>
      DreamTeamRequest.execute(
        OrderingKeyPath(season = Some(season),
          leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitData.leagueUnitId)),
        statsType,
        sortBy
      ).map(players => Ok(Json.toJson(players)))
    )
  }
}
