package databases.requests.playerstats.player

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.Roles
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.player.PlayerSalaryTSI
import models.web.{PlayersParameters, RestStatisticsParameters}
import sqlbuilder.{Select, SqlBuilder}

object PlayerSalaryTSIRequest extends ClickhousePlayerRequest[PlayerSalaryTSI] {
  override val sortingColumns: Seq[String] = Seq("age", "tsi", "salary")

  override val rowParser: RowParser[PlayerSalaryTSI] = PlayerSalaryTSI.mapper

  override def buildSql(orderingKeyPath: OrderingKeyPath, parameters: RestStatisticsParameters, playersParameters: PlayersParameters, role: Option[String], round: Int): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
        "league_id",
        "team_name",
        "team_id",
        "league_unit_name",
        "league_unit_id",
        "player_id",
        "first_name",
        "last_name",
        "((age * 112) + days)" as "age",
        "tsi",
        "salary",
        "nationality",
        ClickhouseRequest.roleIdCase("role_id") as "role"
      ).from("hattrick.player_stats")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .isLeagueMatch
        .round(round)
        .role(role.map(Roles.reverseMapping))
        .nationality(playersParameters.nationality)
        .age.greaterEqual(playersParameters.minAge.map(_ * 112))
        .age.lessEqual(playersParameters.maxAge.map(_ * 112 + 111))
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection.toSql),
        "player_id".to(parameters.sortingDirection.toSql)
      ).limit(page = parameters.page, pageSize = parameters.pageSize)
  }
}
