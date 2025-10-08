package controllers

import databases.dao.RestClickhouseDAO
import databases.requests.OrderingKeyPath
import databases.requests.model.overview.{PlayerStatOverview, TotalOverview}
import databases.requests.overview.charts.{AverageGoalsChartRequest, AverageHatstatsChartRequest, AverageSpectatorsChartRequest, FormationsChartRequest, GoalsNumberOverviewChartRequest, InjuriesNumberOverviewChartRequest, NewTeamsNumberChartRequest, NumbersOverviewChartRequest, PlayersNumberOverviewChartRequest, RedCardsNumberOverviewRequest, TeamsNumberOverviewChartRequest, YellowCardsNumberOverviewRequest}

import java.util.Date
import javax.inject.Inject
import models.web.rest.LevelData
import models.web.rest.LevelData.Rounds
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.RestOverviewStatsService
import service.leagueinfo.{Finished, LeagueInfoService, Loading, Scheduled}
import zio.{ZIO, ZLayer}

case class WorldLoadingInfo(proceedCountries: Int,
                            nextCountry: Option[(Int, String, Date)],
                            currentCountry: Option[(Int, String)])

object WorldLoadingInfo {
  implicit val writes: OWrites[WorldLoadingInfo] = Json.writes[WorldLoadingInfo]
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
}

class RestOverviewController @Inject()(val controllerComponents: ControllerComponents,
                                       val restClickhouseDAO: RestClickhouseDAO,
                                       val restOverviewStatsService: RestOverviewStatsService,
                                       val leagueInfoService: LeagueInfoService)
                                  extends RestController {
  private val lastLeagueId = 100

  def getWorldData: Action[AnyContent] = asyncZio {
    val countries = leagueInfoService.idToStringCountryMap

    val leagueInfoCountries = leagueInfoService.leagueInfo.leagueInfo.values.toSeq
    val proceedCountries = leagueInfoCountries.count(_.loadingInfo == Finished)

    val worldLoadingInfo = if(proceedCountries == leagueInfoService.leagueInfo.leagueInfo.size) {
      None
    } else {
      val nextCountry = leagueInfoCountries.filter(_.loadingInfo match {
        case Scheduled(_) => true
        case _ => false
      }).sortBy(_.loadingInfo.asInstanceOf[Scheduled].date).headOption
        .map(leagueInfo => (leagueInfo.leagueId, leagueInfo.league.englishName, leagueInfo.loadingInfo.asInstanceOf[Scheduled].date))

      val currentCountry = leagueInfoCountries.find(_.loadingInfo match {
        case Loading => true
        case _ => false
      })
        .map(leagueInfo => (leagueInfo.leagueId, leagueInfo.league.englishName))

      Some(WorldLoadingInfo(proceedCountries, nextCountry, currentCountry))
    }

    val worldData = WorldData(countries = countries,
      seasonOffset = 0,
      seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(lastLeagueId),
      currency = "$",
      currencyRate = 10.0d,
      worldLoadingInfo,
      isWorldData = "true"
      )

    ZIO.succeed(worldData)
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

  def totalOverview(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    //in case divisionLevel or league is Empty - return nothing
    if (leagueId.isDefined && !leagueInfoService.leagueInfo.leagueInfo.contains(leagueId.get)
    ||
      leagueId.isDefined && divisionLevel.isDefined
            && !leagueInfoService.leagueInfo(leagueId.get).seasonInfo(season).roundInfo(round).divisionLevelInfo.contains(divisionLevel.get)) {
      ZIO.succeed(TotalOverview.empty())
    } else {
      restOverviewStatsService.totalOverview(season, round, leagueId, divisionLevel)
    }
  }

  private def numbersChart(request: NumbersOverviewChartRequest)(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    val currentRound = leagueId.map(lid => leagueInfoService.leagueInfo.currentRound(lid)).getOrElse(leagueInfoService.lastFullRound())
    val currentSeason = leagueId.map(lid => leagueInfoService.leagueInfo.currentSeason(lid)).getOrElse(leagueInfoService.lastFullSeason())

    request.execute(
      orderingKeyPath = OrderingKeyPath(leagueId = leagueId, divisionLevel = divisionLevel),
      currentRound = currentRound,
      currentSeason = currentSeason
    ).provide(ZLayer.succeed(restClickhouseDAO))
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
    val currentRound = leagueId.map(lid => leagueInfoService.leagueInfo.currentRound(lid)).getOrElse(leagueInfoService.lastFullRound())
    val currentSeason = leagueId.map(lid => leagueInfoService.leagueInfo.currentSeason(lid)).getOrElse(leagueInfoService.lastFullSeason())

    FormationsChartRequest.execute(
      orderingKeyPath = OrderingKeyPath(leagueId = leagueId, divisionLevel = divisionLevel),
      currentRound = currentRound,
      currentSeason = currentSeason
    ).provide(ZLayer.succeed(restClickhouseDAO))
  }

  def averageHatstatNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] =
    numbersChart(AverageHatstatsChartRequest)(leagueId, divisionLevel)

  def averageSpectatorNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] =
    numbersChart(AverageSpectatorsChartRequest)(leagueId, divisionLevel)

  def averageGoalNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] =
    numbersChart(AverageGoalsChartRequest)(leagueId, divisionLevel)

  def newTeamNumbersChart(leagueId: Option[Int], divisionLevel: Option[Int]): Action[AnyContent] = asyncZio {
    val currentRound = leagueId.map(lid => leagueInfoService.leagueInfo.currentRound(lid)).getOrElse(leagueInfoService.lastFullRound())
    val currentSeason = leagueId.map(lid => leagueInfoService.leagueInfo.currentSeason(lid)).getOrElse(leagueInfoService.lastFullSeason())
    NewTeamsNumberChartRequest.execute(
      orderingKeyPath = OrderingKeyPath(leagueId = leagueId, divisionLevel = divisionLevel),
      currentRound = currentRound,
      currentSeason = currentSeason
    ).provide(ZLayer.succeed(restClickhouseDAO))
  }
}
