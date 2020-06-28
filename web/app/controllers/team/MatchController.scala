package controllers

import java.util.Date

import com.blackmorse.hattrick.model.enums.MatchType
import databases.ClickhouseDAO
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import play.api.mvc.ControllerComponents

import collection.JavaConverters._
import scala.concurrent.Future
import models.clickhouse.TeamMatchInfo

import scala.concurrent.ExecutionContext.Implicits.global

case class WebTeamMatch(teamMatchInfo: TeamMatchInfo, date: Date,
                        homeTeamId: Long, homeTeamName: String, homeTeamGoals: Int,
                        awayTeamId: Long, awayTeamName: String, awayTeamGoals: Int)

@Singleton
class MatchController @Inject()(val controllerComponents: ControllerComponents,
                                val clickhouseDAO: ClickhouseDAO,
                                val hattrick: Hattrick) {
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

//        val (teamGoals, enemyGoals, enemyTeamName, enemyTeamId) = if (htMatch.getHomeTeam.getHomeTeamId == chMatch.teamId) {
//          (htMatch.getHomeGoals, htMatch.getAwayGoals, htMatch.getAwayTeam.getAwayTeamName, htMatch.getAwayTeam.getAwayTeamId)
//        } else {
//          (htMatch.getAwayGoals, htMatch.getHomeGoals, htMatch.getHomeTeam.getHomeTeamName, htMatch.getHomeTeam.getHomeTeamId)
//        }

//        WebTeamMatch(chMatch, enemyTeamId, enemyTeamName, teamGoals, enemyGoals)

          WebTeamMatch(chMatch, htMatch.getMatchDate,
            htMatch.getHomeTeam.getHomeTeamId, htMatch.getHomeTeam.getHomeTeamName, htMatch.getHomeGoals,
            htMatch.getAwayTeam.getAwayTeamId, htMatch.getAwayTeam.getAwayTeamName, htMatch.getAwayGoals
          )
      })
    }
  }
}
