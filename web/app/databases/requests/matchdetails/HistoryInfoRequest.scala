package databases.requests.matchdetails

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest
import models.clickhouse.HistoryInfo
import sqlbuilder.{Select, SqlBuilder}
import databases.dao.SqlBuilderParameters

import scala.concurrent.Future

object HistoryInfoRequest extends ClickhouseRequest[HistoryInfo] {
  override val rowParser: RowParser[HistoryInfo] = HistoryInfo.mapper

  def execute(leagueId: Option[Int],
              season: Option[Int],
              round: Option[Int])(implicit restClickhouseDAO: RestClickhouseDAO): Future[List[HistoryInfo]] = {
    restClickhouseDAO.execute(builder(leagueId, season, round).sqlWithParameters().build, rowParser)
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
        "count()" as "cnt"
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
