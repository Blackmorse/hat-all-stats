package databases.requests.promotions

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.model.promotions.Promotion
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.sqlbuilder.{Select, SqlBuilder}

import scala.concurrent.Future

object PromotionsRequest extends ClickhouseRequest[Promotion] {
  override val rowParser: RowParser[Promotion] = Promotion.promotionMapper

  def execute(orderingKeyPath: OrderingKeyPath, season: Int)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[Promotion]] = {

    val divisionLevelCondition = orderingKeyPath.divisionLevel.map(level => s"AND (up_division_level = $level or up_division_level = ${level - 1})")
    val hasLeagueUnitIdCondition = orderingKeyPath.leagueUnitId.map(id => s"AND (has(`going_down_teams.league_unit_id`, $id) OR has(`going_up_teams.league_unit_id`, $id))")
    val hasTeamIdCondition = orderingKeyPath.teamId.map(id => s"AND (has(`going_down_teams.team_id`, $id) OR has(`going_up_teams.team_id`, $id) )")

    import SqlBuilder.implicits._
    val newBuilder = Select(
        "season",
        "league_id",
        "up_division_level",
        "promotion_type",
        "`going_down_teams.team_id`",
        "`going_down_teams.team_name`",
        "`going_down_teams.division_level`",
        "`going_down_teams.league_unit_id`",
        "`going_down_teams.league_unit_name`",
        "`going_down_teams.position`",
        "`going_down_teams.points`",
        "`going_down_teams.diff`",
        "`going_down_teams.scored`",
        "`going_up_teams.team_id`",
        "`going_up_teams.team_name`",
        "`going_up_teams.division_level`",
        "`going_up_teams.league_unit_id`",
        "`going_up_teams.league_unit_name`",
        "`going_up_teams.position`",
        "`going_up_teams.points`",
        "`going_up_teams.diff`",
        "`going_up_teams.scored`"
      ).from("hattrick.promotions")
      .where
        .leagueId(orderingKeyPath.leagueId)
        .season(season)
        .and(divisionLevelCondition)
        .and(hasLeagueUnitIdCondition)
        .and(hasTeamIdCondition)

    restClickhouseDAO.execute(newBuilder.build, rowParser)
  }
}
