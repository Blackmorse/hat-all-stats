package com.blackmorse.hattid.web.databases.requests.promotions

import anorm.RowParser
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.model.promotions.Promotion
import com.blackmorse.hattid.web.databases.requests.{ClickhouseRequest, OrderingKeyPath}
import sqlbuilder.Select
import ClickhouseRequest.*
import com.blackmorse.hattid.web.models.web.HattidError
import zio.{IO, ZIO}

object PromotionsRequest extends ClickhouseRequest[Promotion] {
  override val rowParser: RowParser[Promotion] = Promotion.promotionMapper

  def execute(orderingKeyPath: OrderingKeyPath, season: Int): DBIO[List[Promotion]] = wrapErrors {

    val divisionLevelCondition = orderingKeyPath.divisionLevel.map(level => s"up_division_level = $level OR up_division_level = ${level - 1}")
    val hasLeagueUnitIdCondition = orderingKeyPath.leagueUnitId.map(id => s"has(`going_down_teams.league_unit_id`, $id) OR has(`going_up_teams.league_unit_id`, $id)")
    val hasTeamIdCondition = orderingKeyPath.teamId.map(id => s"has(`going_down_teams.team_id`, $id) OR has(`going_up_teams.team_id`, $id) ")

    import sqlbuilder.SqlBuilder.implicits._
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

    RestClickhouseDAO.executeZIO(newBuilder.sqlWithParameters().build, rowParser)
  }
}
