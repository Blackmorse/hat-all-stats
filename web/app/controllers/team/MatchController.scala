package controllers

import java.util.Date
import com.blackmorse.hattrick.model.enums.MatchType
import databases.ClickhouseDAO
import hattrick.Hattrick

import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}

import collection.JavaConverters._
import scala.concurrent.Future
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
                                val hattrick: Hattrick,
                                val similarMatchesService: SimilarMatchesService)  extends BaseController {
  def matchesFuture(webTeamDetails: WebTeamDetails, season: Int) = {
    val htMatchesFuture = Future(hattrick.api.matchesArchive().teamId(webTeamDetails.teamId).season(season).execute())

    val chMatchesFuture = clickhouseDAO.teamMatchesForSeason(season = season,
      leagueId = webTeamDetails.leagueInfo.leagueId,
      teamId = webTeamDetails.teamId)

    chMatchesFuture.zipWith(htMatchesFuture) { case (chMatches, htMatches) =>
      val htMatchesMap = htMatches.getTeam.getMatchList.asScala
        .filter(matc => matc.getMatchType == MatchType.LEAGUE_MATCH)
        .map(matc => (matc.getMatchId, matc)).toMap

      chMatches.map(chMatch => {
        val htMatch = htMatchesMap(chMatch.matchId)
          WebTeamMatch(chMatch, htMatch.getMatchDate,
            htMatch.getHomeTeam.getHomeTeamId, htMatch.getHomeTeam.getHomeTeamName, htMatch.getHomeGoals,
            htMatch.getAwayTeam.getAwayTeamId, htMatch.getAwayTeam.getAwayTeamName, htMatch.getAwayGoals
          )
      })
    }
  }

  def similarMatches(matchId: Long, accuracy: Double) = Action.async{ implicit request =>
    similarMatchesService.similarMatchesStats(matchId, accuracy)
      .map(similarMatchesStats =>
        similarMatchesStats.map(s => Ok(Json.toJson(s))).getOrElse(Ok("")))
  }
}
