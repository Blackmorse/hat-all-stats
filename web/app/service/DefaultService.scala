package service

import databases.ClickhouseDAO
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import play.api.Configuration

import collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DefaultService @Inject() (val hattrick: Hattrick,
                                val clickhouseDAO: ClickhouseDAO,
                                val configuration: Configuration) {
  lazy val leagueIdToCountryNameMap = hattrick.api.worldDetails().execute()
    .getLeagueList.asScala.map(league => league.getLeagueId -> league)
    .toMap

  lazy val currentSeason = configuration.get[Int]("hattrick.currentSeason")

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
}

object DefaultService {
  val PAGE_SIZE = 8
}
