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

  def seasonForLeagueId(season: Int, leagueId: Int) = leagueIdToCountryNameMap(leagueId).getSeasonOffset + season

  def currentRound(leagueId: Int) = Math.min(leagueIdToCountryNameMap(leagueId).getMatchRound - 1, 14)
}

object DefaultService {
  val PAGE_SIZE = 16
}
