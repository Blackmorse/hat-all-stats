package controllers

import chpp.leaguedetails.LeagueDetailsRequest
import chpp.leaguedetails.models.LeagueDetails
import chpp.leaguefixtures.LeagueFixturesRequest
import chpp.leaguefixtures.models.LeagueFixtures
import chpp.search.SearchRequest
import chpp.search.models.{Search, SearchType}
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
import hattrick.ChppClient
import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import models.web._
import play.api.libs.json.{Json, OWrites, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.{LeagueInfoService, LoadingInfo}
import service.leagueunit.LeagueUnitCalculatorService
import utils.{CurrencyUtils, LeagueNameParser, Romans}

import javax.inject.Inject
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
  implicit val writes: OWrites[RestLeagueUnitData] = Json.writes[RestLeagueUnitData]
}

class RestLeagueUnitController @Inject() (val chppClient: ChppClient,
                                           val controllerComponents: ControllerComponents,
                                          val leagueInfoService: LeagueInfoService,
                                          implicit val restClickhouseDAO: RestClickhouseDAO,
                                          val leagueUnitCalculatorService: LeagueUnitCalculatorService) extends RestController {
  case class LongWrapper(id: Long)
  implicit val writes: OWrites[LongWrapper] = Json.writes[LongWrapper]

  def leagueUnitIdByName(leagueUnitName: String, leagueId: Int): Action[AnyContent] = Action.async{ implicit request =>
    findLeagueUnitIdByName(leagueUnitName, leagueId)
      .map(id => Ok(Json.toJson(LongWrapper(id))))
  }

  private def findLeagueUnitIdByName(leagueUnitName: String, leagueId: Int): Future[Long] = {
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
          searchType = Some(SearchType.SERIES),
          searchLeagueId = Some(leagueId),
          searchString = Some(actualDivision + actualNumber)
        )).map(_.searchResults.head.resultId)
      } else {
        chppClient.execute[Search, SearchRequest](SearchRequest(
          searchType = Some(SearchType.SERIES),
          searchLeagueId = Some(leagueId),
          searchString = Some(leagueUnitName)
        )).map(_.searchResults.head.resultId)
      }
    }
  }

  private def leagueUnitDataFromId(leagueUnitId: Int): Future[RestLeagueUnitData] = {
    chppClient.execute[LeagueDetails, LeagueDetailsRequest](LeagueDetailsRequest(leagueUnitId = Some(leagueUnitId)))
      .map(leagueDetails => {
        val league = leagueInfoService.leagueInfo(leagueDetails.leagueId).league

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
      })
  }

  def getLeagueUnitData(leagueUnitId: Int): Action[AnyContent] = Action.async { implicit request =>
    leagueUnitDataFromId(leagueUnitId)
      .map(rlud =>  Ok(Json.toJson(rlud)))
  }

  private def stats[T](chRequest: ClickhouseStatisticsRequest[T],
                       leagueUnitId: Int,
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
                               leagueUnitId: Int,
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

  def teamHatstats(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamHatstatsRequest, leagueUnitId, restStatisticsParameters)

  def playerGoalGames(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerGamesGoalsRequest, leagueUnitId, restStatisticsParameters, playersParameters)

  def playerCards(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters,
                  playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerCardsRequest, leagueUnitId, restStatisticsParameters, playersParameters)

  def playerTsiSalary(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerSalaryTSIRequest, leagueUnitId, restStatisticsParameters, playersParameters)

  def playerRatings(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerRatingsRequest, leagueUnitId, restStatisticsParameters, playersParameters)

  def playerInjuries(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(PlayerInjuryRequest, leagueUnitId, restStatisticsParameters)

  def teamSalaryTsi(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters, playedInLastMatch: Boolean): Action[AnyContent] = Action.async{ implicit request =>
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

  def teamCards(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async { implicit requst =>
    leagueUnitDataFromId(leagueUnitId).flatMap(leagueUnitData =>
      TeamCardsRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitData.leagueUnitId)),
        restStatisticsParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
    )
  }

  def teamRatings(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamRatingsRequest, leagueUnitId, restStatisticsParameters)

  def teamAgeInjuries(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamAgeInjuryRequest, leagueUnitId, restStatisticsParameters)

  def teamPowerRatings(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamPowerRatingsRequest, leagueUnitId, restStatisticsParameters)

  def teamFanclubFlags(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamFanclubFlagsRequest, leagueUnitId, restStatisticsParameters)

  def teamStreakTrophies(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamStreakTrophiesRequest, leagueUnitId, restStatisticsParameters)

  def topMatches(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchTopHatstatsRequest, leagueUnitId, restStatisticsParameters)

  def surprisingMatches(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchSurprisingRequest, leagueUnitId, restStatisticsParameters)

  def matchSpectators(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchSpectatorsRequest, leagueUnitId, restStatisticsParameters)

  def teamPositions(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async{ implicit request =>
    chppClient.execute[LeagueDetails, LeagueDetailsRequest](LeagueDetailsRequest(leagueUnitId = Some(leagueUnitId)))
      .flatMap(leagueDetails => {
        val offsettedSeason = leagueInfoService.getRelativeSeasonFromAbsolute(restStatisticsParameters.season, leagueDetails.leagueId)

        chppClient.execute[LeagueFixtures, LeagueFixturesRequest](LeagueFixturesRequest(leagueLevelUnitId = Some(leagueUnitId), season = Some(offsettedSeason)))
          .map(leagueFixture => {
            val round = restStatisticsParameters.statsType.asInstanceOf[Round].round

            val teams = leagueUnitCalculatorService.calculate(leagueFixture, Some(round),
                    restStatisticsParameters.sortBy, restStatisticsParameters.sortingDirection)

            Ok(Json.toJson(teams))
          })
      })
  }

  def promotions(leagueUnitId: Int): Action[AnyContent] = Action.async{ implicit request =>
    leagueUnitDataFromId(leagueUnitId).flatMap(leagueUnitData =>
      PromotionsRequest.execute(OrderingKeyPath(
          leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitData.leagueUnitId)), leagueInfoService.leagueInfo.currentSeason(leagueUnitData.leagueId)))
      .map(PromotionWithType.convert)
      .map(result => Ok(Json.toJson(result)))
  }

  def dreamTeam(season: Int, leagueUnitId: Int, sortBy: String, statsType: StatsType): Action[AnyContent] = Action.async{ implicit request =>
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
