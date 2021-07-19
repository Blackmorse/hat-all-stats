package databases.requests.overview

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.FormationsOverview
import databases.sqlbuilder.SqlBuilder

import scala.concurrent.Future

object FormationsOverviewRequest extends ClickhouseOverviewRequest[FormationsOverview] {
  override val sql: String = """
     |SELECT
     |  formation, count() AS count
     |FROM hattrick.match_details
     |__where__
     |GROUP BY formation  ORDER BY count DESC""".stripMargin

  override val rowParser: RowParser[FormationsOverview] = FormationsOverview.mapper

  override def execute(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int])
                      (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[FormationsOverview]] = {
    import SqlBuilder.implicits._

    val builder = SqlBuilder("", newApi = true)
      .select(
        "formation",
        "count()" as "count"
      )
      .from("hattrick.match_details")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch
      .limit(limit)
      .groupBy("formation")
      .orderBy("count".desc)

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
