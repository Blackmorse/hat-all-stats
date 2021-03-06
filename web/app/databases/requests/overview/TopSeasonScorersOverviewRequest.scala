package databases.requests.overview

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.PlayerStatOverview
import databases.sqlbuilder.SqlBuilder

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
      |    __where__
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

    val builder = SqlBuilder(sql)
      .where
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .round.lessEqual(round)
        .page(0)
        .pageSize(limit)

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
