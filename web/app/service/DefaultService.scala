package service

import databases.ClickhouseDAO
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import utils.Romans

import collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class DefaultService @Inject() (val hattrick: Hattrick,
                                val clickhouseDAO: ClickhouseDAO,
                                val configuration: Configuration) {
  lazy val leagueIdToCountryNameMap = hattrick.api.worldDetails().execute()
    .getLeagueList.asScala.map(league => league.getLeagueId -> league)
    .toMap

  lazy val currentSeason = leagueIdToCountryNameMap(1).getSeason

  lazy val defaultLeagueId = configuration.get[Int]("hattrick.defaultLeagueId")

  lazy val leagueNumbersMap = Map(1 -> Seq(1),
    2 -> (1 to 4),
    3 -> (1 to 16),
    4 -> (1 to 64),
    5 -> (1 to 256),
    6 -> (1 to 1024),
    7 -> (1 to 1024),
    8 -> (1 to 2048),
    9 -> (1 to 2048)
  )

  lazy val leagueSeasons = clickhouseDAO.seasonsForLeagues().groupBy(_.leagueId).mapValues(_.map(_.season)).mapValues(_.toSeq)

  def seasonsWithLinks(leagueId: Int, seasonLinkFunction: Int => String): Seq[(String, String)] = leagueSeasons(leagueId)
    .map(season => (season.toString, seasonLinkFunction(season)))

  def seasonForLeagueId(season: Int, leagueId: Int) = leagueIdToCountryNameMap(leagueId).getSeasonOffset + season

  def currentRound(leagueId: Int) = Math.min(leagueIdToCountryNameMap(leagueId).getMatchRound - 1, 14)

  def firstIdOfDivisionLeagueUnit(leagueId: Int, level: Int): Future[Long] = Future {
    if (leagueId == 1 /* Sweden */) {
      if(level == 1) {
        hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString("Ia")
          .execute().getSearchResults.get(0).getResultId - 1
      } else if (level == 2) {
        hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString("Ia")
          .execute().getSearchResults.get(0).getResultId
      } else if (level == 3) {
        hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString("IIa")
          .execute().getSearchResults.get(0).getResultId
      } else {
        hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString(Romans(level - 1) + ".1")
          .execute().getSearchResults.get(0).getResultId
      }
    } else {
      if (level == 1) {
        hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString("II.1")
          .execute().getSearchResults.get(0).getResultId - 1
      } else {
        hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString(Romans(level) + ".1")
          .execute().getSearchResults.get(0).getResultId
      }
    }
  }
}

object DefaultService {
  val PAGE_SIZE = 8
}
