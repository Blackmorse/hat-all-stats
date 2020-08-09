package controllers

import hattrick.Hattrick
import play.api.mvc.BaseController
import models.web.ViewDataFactory
import service.DefaultService
import service.LeagueInfoService
import databases.ClickhouseDAO
import com.google.inject.{Inject, Singleton}
import play.api.mvc.ControllerComponents
import utils.Romans

import scala.concurrent.Future
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import models.web.StatisticsParameters
import databases.clickhouse.StatisticsCHRequest
import models.web.Desc
import models.web.Avg

case class RestLeagueData(leagueId: Int, leagueName: String, divisionLevels: Seq[String])

object RestLeagueData {
  implicit val writes = Json.writes[RestLeagueData]
}

@Singleton
class RestLeagueController @Inject() (val controllerComponents: ControllerComponents,
                                  implicit val clickhouseDAO: ClickhouseDAO,
                                  val leagueInfoService: LeagueInfoService,
                                  val defaultService: DefaultService,
                                  val viewDataFactory: ViewDataFactory,
                                  val hattrick: Hattrick) extends BaseController  {
    
    def getLeagueData(leagueId: Int) = Action.async {implicit request => 
      val leagueName = leagueInfoService.leagueInfo(leagueId).league.getEnglishName
      val numberOfDivisions = leagueInfoService.leagueInfo(leagueId).league.getNumberOfLevels
      val divisionLevels = (1 to numberOfDivisions).map(Romans(_))

      Future(Ok(Json.toJson(RestLeagueData(leagueId, leagueName, divisionLevels))))
    }

    def teamHatstats(leagueId: Int, statisticsParametersOps: Option[StatisticsParameters]) = Action.async { implicit request =>
      val statisticsParameters = statisticsParametersOps.getOrElse(
          StatisticsParameters(season = leagueInfoService.leagueInfo.currentSeason(leagueId),
            page = 0,
            statsType = Avg,
            sortBy = "hatstats",
            pageSize = DefaultService.PAGE_SIZE,
            sortingDirection = Desc
          )
      )

      StatisticsCHRequest.bestHatstatsTeamRequest.execute(
        leagueId = Some(leagueId),
        statisticsParameters = statisticsParameters)
      .map(teamRatings => {
        Ok(Json.toJson(teamRatings))
      })        
    }

    def leagueUnits(leagueId: Int, statisticsParametersOps: Option[StatisticsParameters]) = Action.async { implicit request =>
      val statisticsParameters = statisticsParametersOps.getOrElse(
        StatisticsParameters(season = leagueInfoService.leagueInfo.currentSeason(leagueId),
          page = 0,
          statsType = Avg,
          sortBy = "hatstats",
          pageSize = DefaultService.PAGE_SIZE,
          sortingDirection = Desc
        )
      )

      StatisticsCHRequest.bestHatstatsLeagueRequest.execute(
        leagueId = Some(leagueId),
        statisticsParameters = statisticsParameters
      ).map(leagueUnits => Ok(Json.toJson(leagueUnits)))
    }
}

