package controllers

import java.util.Date
import databases.dao.ClickhouseDAO

import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}

import models.clickhouse.TeamMatchInfo
import play.api.libs.json.Json
import service.SimilarMatchesService

import scala.concurrent.ExecutionContext.Implicits.global

case class WebTeamMatch(teamMatchInfo: TeamMatchInfo, date: Date,
                        homeTeamId: Long, homeTeamName: String, homeTeamGoals: Int,
                        awayTeamId: Long, awayTeamName: String, awayTeamGoals: Int)

@Singleton
class MatchController @Inject()(val controllerComponents: ControllerComponents,
                                val clickhouseDAO: ClickhouseDAO,
                                val similarMatchesService: SimilarMatchesService)  extends BaseController {

  def similarMatches(matchId: Long, accuracy: Double) = Action.async{ implicit request =>
    similarMatchesService.similarMatchesStats(matchId, accuracy)
      .map(similarMatchesStats =>
        similarMatchesStats.map(s => Ok(Json.toJson(s))).getOrElse(Ok("")))
  }
}
