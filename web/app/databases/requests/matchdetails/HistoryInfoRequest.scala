package databases.requests.matchdetails

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest
import databases.requests.ClickhouseRequest.DBIO
import models.clickhouse.HistoryInfo
import sqlbuilder.{Select, SqlBuilder}
import zio.ZIO

import scala.concurrent.Future

object HistoryInfoRequest extends ClickhouseRequest[HistoryInfo] {
  override val rowParser: RowParser[HistoryInfo] = HistoryInfo.mapper

  def execute(leagueId: Option[Int],
              season: Option[Int],
              round: Option[Int]): DBIO[List[HistoryInfo]] = wrapErrors {
    for {
      restClickhouseDAO <- ZIO.service[RestClickhouseDAO]
      result <- restClickhouseDAO.executeZIO(builder(leagueId, season, round).sqlWithParameters().build, rowParser)
    } yield result
  }

  def builder(leagueId: Option[Int],
              season: Option[Int],
              round: Option[Int]): SqlBuilder = {
    import sqlbuilder.SqlBuilder.implicits._
    Select(
        "season",
        "league_id",
        "division_level",
        "round",
        "count()" `as` "cnt"
      ).from("hattrick.match_details")
      .where
        .leagueId(leagueId)
        .season(season)
        .round(round)
        .isLeagueMatch
      .groupBy(
        "season",
        "league_id",
        "division_level",
        "round"
      ).orderBy(
        "season".asc,
        "league_id".asc,
        "division_level".asc,
        "round".asc
    )
  }
}
