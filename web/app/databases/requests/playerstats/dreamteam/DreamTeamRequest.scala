package databases.requests.playerstats.dreamteam

import anorm.RowParser
import databases.{RestClickhouseDAO, SqlBuilder}
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.player.DreamTeamPlayer
import models.web.{Accumulate, Round, StatsType}

import scala.concurrent.Future

object DreamTeamRequest extends ClickhouseRequest[DreamTeamPlayer] {
  val oneRoundSql =
    """
      |SELECT
      |    player_id,
      |    first_name,
      |    last_name,
      |    team_id,
      |    team_name,
      |    league_unit_id,
      |    league_unit_name,
      |    round,
      |    caseWithExpression(role_id,
      |       100, 'keeper',
      |       101, 'wingback',
      |       102, 'defender',
      |       103, 'defender',
      |       104, 'defender',
      |       105, 'wingback',
      |       106, 'winger',
      |       107, 'midfielder',
      |       108, 'midfielder',
      |       109, 'midfielder',
      |       110, 'winger',
      |       111, 'forward',
      |       112, 'forward',
      |       113, 'forward',
      |     '') AS role,
      |    rating,
      |    rating_end_of_match
      |FROM hattrick.player_stats
      |__where__ AND (round = __round__) AND (role_id != 0)
      |ORDER BY __sortBy__
      |LIMIT 4 BY role
      |""".stripMargin

  val aggregateSql =
    """SELECT * FROM (
      |SELECT
      |    player_id,
      |    first_name,
      |    last_name,
      |    team_id,
      |    team_name,
      |    league_unit_id,
      |    league_unit_name,
      |    round,
      |    caseWithExpression(role_id,
      |       100, 'keeper',
      |       101, 'wingback',
      |       102, 'defender',
      |       103, 'defender',
      |       104, 'defender',
      |       105, 'wingback',
      |       106, 'winger',
      |       107, 'midfielder',
      |       108, 'midfielder',
      |       109, 'midfielder',
      |       110, 'winger',
      |       111, 'forward',
      |       112, 'forward',
      |       113, 'forward',
      |     '') AS role,
      |    rating,
      |    rating_end_of_match
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

    val sql = statsType match {
      case Accumulate => aggregateSql
      case Round(round) => oneRoundSql.replace("__round__", round.toString)
    }

    val builder = SqlBuilder(sql.replace("__sortBy__", sortString))
      .applyParameters(orderingKeyPath)

    orderingKeyPath.season.foreach(builder.season)

    val build = builder.build

    restClickhouseDAO.execute(build, rowParser)
  }
}
