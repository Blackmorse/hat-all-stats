package databases.requests.matchdetails

import anorm.RowParser
import databases.SqlBuilder
import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.`match`.TeamMatch

import scala.concurrent.Future

object TeamMatchesRequest extends ClickhouseRequest[TeamMatch] {
  val sql =
    """SELECT
      |  season,
      |  league_id,
      |  dt,
      |  round,
      |  team_name,
      |  team_id,
      |  league_unit_name,
      |  league_unit_id,
      |  opposite_team_name,
      |  opposite_team_id,
      |  match_id,
      |  is_home_match,
      |  goals,
      |  enemy_goals,
      |  formation,
      |  opposite_formation,
      |  tactic_type,
      |  tactic_skill,
      |  opposite_tactic_type,
      |  opposite_tactic_skill,
      |  rating_midfield,
      |  rating_right_def,
      |  rating_mid_def,
      |  rating_left_def,
      |  rating_right_att,
      |  rating_mid_att,
      |  rating_left_att,
      |  opposite_rating_midfield,
      |  opposite_rating_left_def,
      |  opposite_rating_mid_def,
      |  opposite_rating_right_def,
      |  opposite_rating_left_att,
      |  opposite_rating_mid_att,
      |  opposite_rating_right_att
      |FROM hattrick.match_details
      |__where__
      |ORDER BY round ASC
      |""".stripMargin

  override val rowParser: RowParser[TeamMatch] = TeamMatch.mapper

  def execute(season: Int, orderingKeyPath: OrderingKeyPath)(implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamMatch]] = {
    val builder = SqlBuilder(sql)
      .where
        .applyParameters(orderingKeyPath)
        .season(season)

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
