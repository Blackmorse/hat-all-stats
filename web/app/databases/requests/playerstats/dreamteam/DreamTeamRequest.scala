package databases.requests.playerstats.dreamteam

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.player.DreamTeamPlayer
import databases.sqlbuilder.SqlBuilder
import models.web.{Accumulate, Round, StatsType}

import scala.concurrent.Future

object DreamTeamRequest extends ClickhouseRequest[DreamTeamPlayer] {
  val oneRoundSql =
    s"""
      |SELECT
      |    league_id,
      |    player_id,
      |    first_name,
      |    last_name,
      |    team_id,
      |    team_name,
      |    league_unit_id,
      |    league_unit_name,
      |    round,
      |    ${ClickhouseRequest.roleIdCase("role_id")} AS role,
      |    rating,
      |    rating_end_of_match,
      |    nationality
      |FROM hattrick.player_stats
      |__where__ AND (role_id != 0)
      |ORDER BY __sortBy__
      |LIMIT 4 BY role
      |""".stripMargin

  val aggregateSql =
    s"""SELECT * FROM (
      |SELECT
      |    league_id,
      |    player_id,
      |    first_name,
      |    last_name,
      |    team_id,
      |    team_name,
      |    league_unit_id,
      |    league_unit_name,
      |    round,
      |    ${ClickhouseRequest.roleIdCase("role_id")} AS role,
      |    rating,
      |    rating_end_of_match,
      |    nationality
      |FROM hattrick.player_stats
      |__where__ AND (role_id != 0)
      |ORDER BY __sortBy__
      |LIMIT 1 by role, player_id
      |)
      |LIMIT 4 BY role
      |""".stripMargin

  override val rowParser: RowParser[DreamTeamPlayer] = DreamTeamPlayer.mapper

  def execute(orderingKeyPath: OrderingKeyPath, statsType: StatsType, sortBy: String)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[DreamTeamPlayer]] = {
    if(!Seq("rating", "rating_end_of_match").contains(sortBy)) {
      throw new Exception("Unknown sorting field")
    }

    val sortString = if (sortBy == "rating") "rating DESC, rating_end_of_match DESC"
    else "rating_end_of_match DESC, rating DESC"

    val (sql, round) = statsType match {
      case Accumulate => (aggregateSql, None)
      case Round(r) => (oneRoundSql, Some(r))
    }

    val build = SqlBuilder(sql.replace("__sortBy__", sortString))
      .where
        .applyParameters(orderingKeyPath)
        .season(orderingKeyPath.season)
        .round(round)
      .build

    restClickhouseDAO.execute(build, rowParser)
  }
}
