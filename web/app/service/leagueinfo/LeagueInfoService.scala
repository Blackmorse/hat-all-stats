package service.leagueinfo

import chpp.worlddetails.WorldDetailsRequest
import chpp.worlddetails.models.WorldDetails
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.HistoryInfoRequest
import hattid.CommonData
import webclients.ChppClient
import play.api.Configuration

import javax.inject.{Inject, Singleton}
import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

@Singleton
class LeagueInfoService @Inject() (val chppClient: ChppClient,
                                   implicit val restClickhouseDAO: RestClickhouseDAO,
                                   val configuration: Configuration) {
  lazy val leagueNumbersMap: Map[Int, Seq[Int]] = Map(1 -> Seq(1),
    2 -> (1 to 4),
    3 -> (1 to 16),
    4 -> (1 to 64),
    5 -> (1 to 256),
    6 -> (1 to 1024),
    7 -> (1 to 1024),
    8 -> (1 to 2048),
    9 -> (1 to 2048)
  )

  def lastFullRound(): Int = {
    //Salvador has last league matches
    leagueInfo.currentRound(CommonData.LAST_SERIES_LEAGUE_ID)
  }

  def lastFullSeason(): Int = {
    leagueInfo.currentSeason(CommonData.LAST_SERIES_LEAGUE_ID)
  }

  def getRelativeSeasonFromAbsolute(season: Int, leagueId: Int): Int = leagueInfo(leagueId).league.seasonOffset + season

  val leagueInfo: LeaguesInfo = {
    val leagueIdToCountryNameMap = Await.result(chppClient.executeUnsafe[WorldDetails, WorldDetailsRequest](WorldDetailsRequest())
      .map(_.leagueList.map(league => league.leagueId -> league)), 60.seconds)


    val leagueHistoryInfos = Await.result(HistoryInfoRequest.execute(None, None, None), 1.minute)
      .groupBy(_.leagueId)

    val seq = for ((lId, league) <- leagueIdToCountryNameMap) yield {
      val leagueId  = lId.toInt
      leagueHistoryInfos.get(leagueId).map(historyInfos =>
        leagueId -> LeagueInfo(leagueId, historyInfos.groupBy(_.season).map{case(season, historyInfos) =>
          season -> SeasonInfo(season, historyInfos.groupBy(_.round).map{case(round, historyInfos) =>
            round -> RoundInfo(round, historyInfos.groupBy(_.divisionLevel).map{case(divisionLevel, historyInfos) =>
              divisionLevel -> DivisionLevelInfo(divisionLevel, historyInfos.head.count)}
            )}
          )}
          , league, Finished))
        .getOrElse(leagueId -> LeagueInfo(leagueId, mutable.Map(), league, Finished))
    }

    LeaguesInfo(seq.toMap)
  }

  val idToStringCountryMap: Seq[(Int, String)] = leagueInfo.leagueInfo
    .toSeq
    .map{case(leagueId, leagueInfo) => (leagueId, leagueInfo.league.englishName)}
    .sortBy(_._2)

  implicit def toMutable[T](map: Map[Int, T]): mutable.Map[Int, T] = {
    mutable.Map(map.toSeq: _*)
  }
}