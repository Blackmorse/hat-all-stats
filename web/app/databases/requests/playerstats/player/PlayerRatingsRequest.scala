package databases.requests.playerstats.player

import anorm.RowParser
import databases.requests.model.Roles
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.player.PlayerRating
import databases.sqlbuilder.{Select, SqlBuilder}
import models.web.{PlayersParameters, RestStatisticsParameters}

object PlayerRatingsRequest extends ClickhousePlayerRequest[PlayerRating]{
  override val sortingColumns: Seq[String] = Seq("age", "rating", "rating_end_of_match")

  override val rowParser: RowParser[PlayerRating] = PlayerRating.mapper

  override def buildSql(orderingKeyPath: OrderingKeyPath,
                        parameters: RestStatisticsParameters,
                        playersParameters: PlayersParameters,
                        role: Option[String], round: Int): SqlBuilder = {
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
        "rating",
        "rating_end_of_match",
        "nationality",
        ClickhouseRequest.roleIdCase("role_id") as "role",
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
        parameters.sortBy.to(parameters.sortingDirection),
        "player_id".to(parameters.sortingDirection)
      ).limit(page = parameters.page, pageSize = parameters.pageSize)
  }
}
