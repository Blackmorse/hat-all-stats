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
        val leagueName = leagueInfoService.leagueInfo(leagueId).league.getEnglishName()
        val numberOfDivisions = leagueInfoService.leagueInfo(leagueId).league.getNumberOfLevels()
        val divisionLevels = (1 to numberOfDivisions).map(Romans(_))

        val leagueData = RestLeagueData(leagueId, leagueName, divisionLevels)

        Future(Ok(Json.toJson(RestLeagueData(leagueId, leagueName, divisionLevels))))
    }
}

