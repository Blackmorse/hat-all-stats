package databases.requests.overview

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.FormationsOverviewRequest.rowParser
import databases.requests.overview.model.PlayerStatOverview
import databases.sqlbuilder.SqlBuilder

import scala.concurrent.Future

object TopSalaryPlayerOverviewRequest extends ClickhouseOverviewRequest[PlayerStatOverview] {
  override val sql: String = """
       |SELECT
       |  league_id,
       |  league_unit_id,
       |  league_unit_name,
       |  team_id,
       |  team_name,
       |  player_id,
       |  first_name,
       |  last_name,
       |  salary AS value,
       |  nationality
       |FROM hattrick.player_stats
       |__where__
       |ORDER BY value DESC
       |__limit__
       |""".stripMargin
  override val rowParser: RowParser[PlayerStatOverview] = PlayerStatOverview.mapper

  override def execute(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int])
                      (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[PlayerStatOverview]] = {
    import SqlBuilder.implicits._
    val builder = new SqlBuilder("", newApi = true)
      .select(
        "league_id",
        "league_unit_id",
        "league_unit_name",
        "team_id",
        "team_name",
        "player_id",
        "first_name",
        "last_name",
        "salary" as "value",
        "nationality"
      )
      .from("hattrick.player_stats")
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
