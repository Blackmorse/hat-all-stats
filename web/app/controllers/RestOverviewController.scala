package controllers

import cache.ZioCacheModule.HattidEnv
import databases.requests.OrderingKeyPath
import databases.requests.model.overview.{PlayerStatOverview, TotalOverview}
import databases.requests.overview.charts.*
import hattid.CommonData
import models.web.rest.LevelData
import models.web.rest.LevelData.Rounds
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.RestOverviewStatsService
import service.leagueinfo.LeagueInfoServiceZIO
import zio.ZIO
import zio.json.{DeriveJsonEncoder, JsonEncoder}

import java.util.Date
import javax.inject.Inject

case class WorldLoadingInfo(proceedCountries: Int,
                            nextCountry: Option[(Int, String, Date)],
                            currentCountry: Option[(Int, String)])

object WorldLoadingInfo {
  implicit val writes: OWrites[WorldLoadingInfo] = Json.writes[WorldLoadingInfo]
  implicit val jsonEncoder: JsonEncoder[WorldLoadingInfo] = DeriveJsonEncoder.gen[WorldLoadingInfo]
  implicit val dateEncoder: JsonEncoder[Date] = JsonEncoder[Long].contramap(_.getTime)
}

case class WorldData(countries: Seq[(Int, String)],
                     seasonOffset: Int,
                     seasonRoundInfo: Seq[(Int, Rounds)],
                     currency: String,
                     currencyRate: Double,
                     loadingInfo: Option[WorldLoadingInfo],
                     isWorldData: String /*TODO for detecting type at TS*/) extends LevelData

object WorldData {
  implicit val writes: OWrites[WorldData] = Json.writes[WorldData]
  implicit val jsonEncoder: JsonEncoder[WorldData] = DeriveJsonEncoder.gen[WorldData]
}

class RestOverviewController @Inject()(val controllerComponents: ControllerComponents,
                                       val restOverviewStatsService: RestOverviewStatsService,
                                       val hattidEnvironment: zio.ZEnvironment[HattidEnv])
                                  extends RestController(hattidEnvironment) {
  private val lastLeagueId = CommonData.LAST_SERIES_LEAGUE_ID

  def getWorldData: Action[AnyContent] = asyncZio {
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      idToStringCountry <- leagueInfoService.idToStringCountryMap
      processedCountriesNumber <- leagueInfoService.getProcessedCountriesNumber
      (nextCountry, currentCountry) <- leagueInfoService.getNextAndCurrentCountry
      countries <- leagueInfoService.countriesNumber
      seasonRoundInfo <- leagueInfoService.seasonRoundInfo(lastLeagueId)
    } yield WorldData(countries = idToStringCountry,
      seasonOffset = 0,
      seasonRoundInfo = seasonRoundInfo,
      currency = "$",
      currencyRate = 10.0d,
      loadingInfo = if (countries == processedCountriesNumber) None
         else Some(WorldLoadingInfo(processedCountriesNumber, nextCountry, currentCountry)),
      isWorldData = "true"
    )
  }

  def numberOverview(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    restOverviewStatsService.numberOverview(season, round, leagueId, divisionLevel)
  }

  def formations(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    restOverviewStatsService.formations(season, round, leagueId, divisionLevel)
  }

  def averagesOverview(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    restOverviewStatsService.averageOverview(season, round, leagueId, divisionLevel)
  }

  def surprisingMatches(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    restOverviewStatsService.surprisingMatches(season, round, leagueId, divisionLevel)
  }

  def topHatstatsTeams(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    restOverviewStatsService.topHatstatsTeams(season, round, leagueId, divisionLevel)
  }

  def topSalaryTeams(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    restOverviewStatsService.topSalaryTeams(season, round, leagueId, divisionLevel)
  }

  def topMatches(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    restOverviewStatsService.topMatches(season, round, leagueId, divisionLevel)
  }

  def topSalaryPlayers(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    restOverviewStatsService.topSalaryPlayers(season, round, leagueId, divisionLevel)
  }

  def topRatingPlayers(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    restOverviewStatsService.topRatingPlayers(season, round, leagueId, divisionLevel)
  }

  def topMatchAttendance(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    restOverviewStatsService.topMatchAttendance(season, round, leagueId, divisionLevel)
  }

  def topTeamVictories(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    restOverviewStatsService.topTeamVictories(season, round, leagueId, divisionLevel)
  }

  def topSeasonScorers(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
      restOverviewStatsService.topSeasonScorers(season, round, leagueId, divisionLevel)
  }

  private def leagueSetButNotExists(leagueId: Option[Int]): ZIO[LeagueInfoServiceZIO, Nothing, Boolean] =
    leagueId.map(id => ZIO.serviceWithZIO[LeagueInfoServiceZIO](serv => !serv.leagueExists(id))).getOrElse(ZIO.succeed(false))
    
  private def divisionLevelSetButNotExists(leagueId: Option[Int], divisionLevel: Option[Int], season: Int, round: Int): ZIO[LeagueInfoServiceZIO, Nothing, Boolean] =
    (leagueId, divisionLevel) match {
      case (Some(lId), Some(dLevel)) => ZIO.serviceWithZIO[LeagueInfoServiceZIO](serv => !serv.divisionLevelExists(lId, season, round, dLevel))
      case _ => ZIO.succeed(false)
    }

  def totalOverview(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    for {
      leagueSetButNotExists        <- leagueSetButNotExists(leagueId)
      divisionLevelSetButNotExists <- divisionLevelSetButNotExists(leagueId, divisionLevel, season, round)
      //in case divisionLevel or league is Empty - return nothing
      res                          <- if (leagueSetButNotExists || divisionLevelSetButNotExists) ZIO.succeed(TotalOverview.empty())
                                      else restOverviewStatsService.totalOverview(season, round, leagueId, divisionLevel)
    } yield res
  }


  private def numbersChart(request: NumbersOverviewChartRequest)(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueIdEffective = leagueId.getOrElse(CommonData.LAST_SERIES_LEAGUE_ID)
      currentSeason     <- leagueInfoService.currentSeason(leagueIdEffective)
      currentRound      <- leagueInfoService.lastRound(leagueIdEffective, season = currentSeason)
      entities          <- request.execute(
                            orderingKeyPath = OrderingKeyPath(leagueId = leagueId, divisionLevel = divisionLevel),
                            currentRound = currentRound,
                            currentSeason = currentSeason)
    } yield entities
  }

  def teamNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] =
    numbersChart(TeamsNumberOverviewChartRequest)(leagueId, divisionLevel)

  def playerNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] =
    numbersChart(PlayersNumberOverviewChartRequest)(leagueId, divisionLevel)

  def goalNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] =
    numbersChart(GoalsNumberOverviewChartRequest)(leagueId, divisionLevel)

  def injuryNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] =
    numbersChart(InjuriesNumberOverviewChartRequest)(leagueId, divisionLevel)

  def yellowCardNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] =
    numbersChart(YellowCardsNumberOverviewRequest)(leagueId, divisionLevel)

  def redCardNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] =
    numbersChart(RedCardsNumberOverviewRequest)(leagueId, divisionLevel)

  def formationsChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueIdEffective = leagueId.getOrElse(CommonData.LAST_SERIES_LEAGUE_ID)
      currentSeason     <- leagueInfoService.currentSeason(leagueIdEffective)
      currentRound      <- leagueInfoService.lastRound(leagueIdEffective, season = currentSeason)
      entities          <- FormationsChartRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = leagueId, divisionLevel = divisionLevel),
        currentRound = currentRound,
        currentSeason = currentSeason)
    } yield entities
  }

  def averageHatstatNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] =
    numbersChart(AverageHatstatsChartRequest)(leagueId, divisionLevel)

  def averageSpectatorNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] =
    numbersChart(AverageSpectatorsChartRequest)(leagueId, divisionLevel)

  def averageGoalNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] =
    numbersChart(AverageGoalsChartRequest)(leagueId, divisionLevel)

  def newTeamNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueIdEffective = leagueId.getOrElse(CommonData.LAST_SERIES_LEAGUE_ID)
      currentSeason     <- leagueInfoService.currentSeason(leagueIdEffective)
      currentRound      <- leagueInfoService.lastRound(leagueIdEffective, season = currentSeason)
      entities          <- NewTeamsNumberChartRequest.execute(
                            orderingKeyPath = OrderingKeyPath(leagueId = leagueId, divisionLevel = divisionLevel),
                            currentRound = currentRound,
                            currentSeason = currentSeason)
    } yield entities
  }
}
