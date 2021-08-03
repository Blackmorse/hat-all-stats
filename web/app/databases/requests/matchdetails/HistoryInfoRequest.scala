package databases.requests.matchdetails

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest
import databases.sqlbuilder.Select
import models.clickhouse.HistoryInfo

import scala.concurrent.Future

object HistoryInfoRequest extends ClickhouseRequest[HistoryInfo] {
  override val rowParser: RowParser[HistoryInfo] = HistoryInfo.mapper

  def execute(leagueId: Option[Int],
              season: Option[Int],
              round: Option[Int])(implicit restClickhouseDAO: RestClickhouseDAO): Future[List[HistoryInfo]] = {
    import databases.sqlbuilder.SqlBuilder.implicits._
    val builder = Select(
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
    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
