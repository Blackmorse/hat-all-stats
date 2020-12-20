package databases.requests.promotions

import anorm.RowParser
import databases.{RestClickhouseDAO, SqlBuilder}
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import models.clickhouse.Promotion

import scala.concurrent.Future

object PromotionsRequest extends ClickhouseRequest[Promotion] {
  override val rowParser: RowParser[Promotion] = Promotion.promotionMapper

  private val sql =
    """SELECT
      |season,
      |league_id,
      |up_division_level,
      |promotion_type,
      |`going_down_teams.team_id`,
      |`going_down_teams.team_name`,
      |`going_down_teams.division_level`,
      |`going_down_teams.league_unit_id`,
      |`going_down_teams.league_unit_name`,
      |`going_down_teams.position`,
      |`going_down_teams.points`,
      |`going_down_teams.diff`,
      |`going_down_teams.scored`,
      |`going_up_teams.team_id`,
      |`going_up_teams.team_name`,
      |`going_up_teams.division_level`,
      |`going_up_teams.league_unit_id`,
      |`going_up_teams.league_unit_name`,
      |`going_up_teams.position`,
      |`going_up_teams.points`,
      |`going_up_teams.diff`,
      |`going_up_teams.scored`
      |FROM hattrick.promotions
      |__where__ __AND__""".stripMargin

  def execute(orderingKeyPath: OrderingKeyPath, season: Int)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[Promotion]] = {

    val divisionLevelWhere = orderingKeyPath.divisionLevel.map(level => s"AND (up_division_level = $level or up_division_level = ${level - 1})").getOrElse("")
    val hasLeagueUnitId = orderingKeyPath.leagueUnitId.map(id => s"AND (has(`going_down_teams.league_unit_id`, $id) OR has(`going_up_teams.league_unit_id`, $id))").getOrElse("")
    val hasTeamId = orderingKeyPath.teamId.map(id => s"AND (has(`going_down_teams.team_id`, $id) OR has(`going_up_teams.team_id`, $id) )").getOrElse("")

    val whereHas = s"$divisionLevelWhere $hasLeagueUnitId $hasTeamId"

    val builder = SqlBuilder(sql.replace("__AND__", whereHas))
      .where
        .leagueId(orderingKeyPath.leagueId)
        .season(season)

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
