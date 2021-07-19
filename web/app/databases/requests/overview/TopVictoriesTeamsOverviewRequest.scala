package databases.requests.overview

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.FormationsOverviewRequest.rowParser
import databases.requests.overview.model.TeamStatOverview
import databases.sqlbuilder.SqlBuilder

import scala.concurrent.Future

object TopVictoriesTeamsOverviewRequest extends ClickhouseOverviewRequest[TeamStatOverview] {
  override val sql: String = """
                               |SELECT
                               |  league_id,
                               |  league_unit_id,
                               |  league_unit_name,
                               |  team_id,
                               |  team_name,
                               |  number_of_victories as value
                               |FROM hattrick.team_details __where__ ORDER BY value DESC
                               | __limit__
                               |""".stripMargin

  override val rowParser: RowParser[TeamStatOverview] = TeamStatOverview.mapper

  override def execute(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int])
                      (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamStatOverview]] = {
    import SqlBuilder.implicits._
    val builder = new SqlBuilder("", newApi = true)
      .select(
        "league_id",
        "league_unit_id",
        "league_unit_name",
        "team_id",
        "team_name",
        "number_of_victories" as "value"
      )
      .from("hattrick.team_details")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
      .orderBy("value".desc)
      .limit(limit)

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
