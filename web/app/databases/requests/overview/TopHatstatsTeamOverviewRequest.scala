package databases.requests.overview

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.NumberOverviewRequest.rowParser
import databases.requests.overview.model.TeamStatOverview
import databases.sqlbuilder.SqlBuilder

import scala.concurrent.Future

object TopHatstatsTeamOverviewRequest extends ClickhouseOverviewRequest[TeamStatOverview] {
  override val sql: String = """
     |SELECT
     |  league_id,
     |  league_unit_id,
     |  league_unit_name,
     |  team_id,
     |  team_name,
     |  rating_midfield * 3 + rating_left_def + rating_mid_def + rating_right_def + rating_left_att + rating_right_att + rating_mid_att AS value
     |FROM hattrick.match_details __where__ ORDER BY value DESC
     | __limit__
     |""".stripMargin

  override val rowParser: RowParser[TeamStatOverview] = TeamStatOverview.mapper

  override def execute(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int])
                      (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamStatOverview]] = {
    import SqlBuilder.implicits._
    import SqlBuilder.fields._
    val builder = new SqlBuilder("", newApi = true)
      .select(
        "league_id",
        "league_unit_id",
        "league_unit_name",
        "team_id",
        "team_name",
        hatstats as "value"
      )
      .from("hattrick.match_details")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch
      .orderBy("value".desc)
      .limit(limit)

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
