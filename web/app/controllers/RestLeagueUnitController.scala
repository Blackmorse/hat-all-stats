package controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import chpp.search.SearchRequest
import chpp.search.models.Search
import com.blackmorse.hattrick.model.enums.SearchType
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.{MatchSpectatorsRequest, MatchSurprisingRequest, MatchTopHatstatsRequest, TeamHatstatsRequest}
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player._
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamdetails.{TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import hattid.CommonData
import hattrick.{ChppClient, Hattrick}
import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import models.web._
import play.api.libs.json.{Json, Writes}
import play.api.mvc.ControllerComponents
import service.LeagueUnitCalculatorService
import service.leagueinfo.{LeagueInfoService, LoadingInfo}
import utils.{LeagueNameParser, Romans}

import javax.inject.Inject
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
                              loadingInfo: LoadingInfo,
                              countries: Seq[(Int, String)]) extends CountryLevelData

object RestLeagueUnitData {
  implicit val writes = Json.writes[RestLeagueUnitData]
}

class RestLeagueUnitController @Inject() (val chppClient: ChppClient,
                                           val controllerComponents: ControllerComponents,
                                          val leagueInfoService: LeagueInfoService,
                                          val hattrick: Hattrick,
                                          implicit val restClickhouseDAO: RestClickhouseDAO,
                                          val leagueUnitCalculatorService: LeagueUnitCalculatorService) extends RestController {
  case class LongWrapper(id: Long)
  implicit val writes = Json.writes[LongWrapper]

  def leagueUnitIdByName(leagueUnitName: String, leagueId: Int) = Action.async{implicit request =>
//    Future(findLeagueUnitIdByName(leagueUnitName, leagueId))
    findLeagueUnitIdByNameAsync(leagueUnitName, leagueId)
      .map(id => Ok(Json.toJson(LongWrapper(id))))
  }

  private def findLeagueUnitIdByNameAsync(leagueUnitName: String, leagueId: Int): Future[Long] = {
    if(leagueUnitName == "I.1") {
      Future(CommonData.higherLeagueMap(leagueId).leagueUnitId)
    } else {
      if(leagueId == 1) { //Sweden
        val (division, number) = LeagueNameParser.getLeagueUnitNumberByName(leagueUnitName)
        val actualDivision = Romans(Romans(division) - 1)
        val actualNumber = if (division == "II" || division == "III") {
          ('a' + number - 1).toChar.toString
        } else {
          "." + number.toString
        }
        chppClient.execute[Search, SearchRequest](SearchRequest(
          searchType = Some(chpp.search.models.SearchType.SERIES),
          searchLeagueId = Some(leagueId),
          searchString = Some(actualDivision + actualNumber)
        )).map(_.searchResults.head.resultId)
      } else {
        chppClient.execute[Search, SearchRequest](SearchRequest(
          searchType = Some(chpp.search.models.SearchType.SERIES),
          searchLeagueId = Some(leagueId),
          searchString = Some(leagueUnitName)
        )).map(_.searchResults.head.resultId)
      }
    }
  }

  private def findLeagueUnitIdByName(leagueUnitName: String, leagueId: Int): Long = {
    if(leagueUnitName == "I.1") {
      CommonData.higherLeagueMap(leagueId).leagueUnitId
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
        teams = leagueDetails.getTeams.asScala.toSeq.map(team => (team.getTeamId.toLong, team.getTeamName)),
        seasonOffset = league.getSeasonOffset,
        seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueDetails.getLeagueId),
        currency = if (league.getCountry.getCurrencyName == null) "$" else league.getCountry.getCurrencyName,
        currencyRate = if (league.getCountry.getCurrencyRate == null) 10.0d else league.getCountry.getCurrencyRate,
        loadingInfo = leagueInfoService.leagueInfo(leagueDetails.getLeagueId).loadingInfo,
        countries = leagueInfoService.idToStringCountryMap)
    }
      )

  def getLeagueUnitData(leagueUnitId: Long) = Action.async {implicit request =>
    leagueUnitDataFromId(leagueUnitId)
      .map(rlud =>  Ok(Json.toJson(rlud)))
  }

  private def stats[T](chRequest: ClickhouseStatisticsRequest[T],
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

  private def playersRequest[T](plRequest: ClickhousePlayerRequest[T],
                               leagueUnitId: Long,
                                restStatisticsParameters: RestStatisticsParameters,
                                playersParameters: PlayersParameters)(implicit writes: Writes[T]) =
    Action.async{ implicit request =>
      leagueUnitDataFromId(leagueUnitId).flatMap(leagueUnitData =>
        plRequest.execute(
          OrderingKeyPath(leagueId = Some(leagueUnitData.leagueId),
            divisionLevel = Some(leagueUnitData.divisionLevel),
            leagueUnitId = Some(leagueUnitId)),
          restStatisticsParameters,
          playersParameters
        ) .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
      )
    }

  def teamHatstats(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamHatstatsRequest, leagueUnitId, restStatisticsParameters)

  def playerGoalGames(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerGamesGoalsRequest, leagueUnitId, restStatisticsParameters, playersParameters)

  def playerCards(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters,
                  playersParameters: PlayersParameters) =
    playersRequest(PlayerCardsRequest, leagueUnitId, restStatisticsParameters, playersParameters)

  def playerTsiSalary(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerSalaryTSIRequest, leagueUnitId, restStatisticsParameters, playersParameters)

  def playerRatings(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerRatingsRequest, leagueUnitId, restStatisticsParameters, playersParameters)

  def playerInjuries(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerInjuryRequest, leagueUnitId, restStatisticsParameters)

  def teamSalaryTsi(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters, playedInLastMatch: Boolean) = Action.async{implicit request =>
    leagueUnitDataFromId(leagueUnitId).flatMap(leagueUnitData =>
      TeamSalaryTSIRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitId)),
        restStatisticsParameters,
        playedInLastMatch
      ) .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
    )
  }

  def teamCards(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) = Action.async { implicit requst =>
    leagueUnitDataFromId(leagueUnitId).flatMap(leagueUnitData =>
      TeamCardsRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitData.leagueUnitId)),
        restStatisticsParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
    )
  }

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
