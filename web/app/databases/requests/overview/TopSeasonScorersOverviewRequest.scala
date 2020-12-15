package databases.requests.overview

import anorm.RowParser
import databases.{RestClickhouseDAO, SqlBuilder}
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.PlayerStatOverview

import scala.concurrent.Future

object TopSeasonScorersOverviewRequest extends ClickhouseOverviewRequest[PlayerStatOverview] {
  override val sql: String =
    """
      |SELECT
      |    any(league_id) AS league_id,
      |    player_id,
      |    first_name,
      |    last_name,
      |    team_id,
      |    argMax(team_name, round) AS team_name,
      |    league_unit_id,
      |    league_unit_name,
      |    sum(goals) AS value,
      |    argMax(nationality, round) as nationality
      |FROM
      |(
      |    SELECT
      |        league_id AS league_id,
      |        player_id,
      |        first_name,
      |        last_name,
      |        team_id,
      |        team_name AS team_name,
      |        league_unit_id,
      |        league_unit_name,
      |        goals,
      |        round,
      |        nationality
      |    FROM hattrick.player_stats
      |    __where__ and round <= __round__
      |) AS inner
      |GROUP BY
      |    player_id,
      |    first_name,
      |    last_name,
      |    team_id,
      |    league_unit_id,
      |    league_unit_name
      |ORDER BY value DESC
      |LIMIT 5""".stripMargin
  override val rowParser: RowParser[PlayerStatOverview] = PlayerStatOverview.mapper

  override def execute(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int])
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[PlayerStatOverview]] = {

    val builder = SqlBuilder(sql.replace("__round__", round.toString))
      .season(season)
      .page(0)
      .pageSize(limit)
    leagueId.foreach(builder.leagueId)
    divisionLevel.foreach(builder.divisionLevel)

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
