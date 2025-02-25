package databases.requests.playerstats.player.stats

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.Roles
import databases.requests.model.player.PlayerGamesGoals
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import models.web.{PlayersParameters, RestStatisticsParameters}
import sqlbuilder.{Select, SqlBuilder}

object PlayerGamesGoalsRequest extends ClickhousePlayerStatsRequest[PlayerGamesGoals] {
  override val sortingColumns: Seq[String] = Seq("games", "played", "scored", "goal_rate", "age")
  override val rowParser: RowParser[PlayerGamesGoals] = PlayerGamesGoals.mapper

  override def buildSql(orderingKeyPath: OrderingKeyPath,
                        parameters: RestStatisticsParameters,
                        playersParameters: PlayersParameters,
                        role: Option[String],
                        round: Int): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
      "any(league_id)" as "league",
      "player_id",
      "first_name",
      "last_name",
      "team_id",
      "argMax(team_name, round)" as "team_name",
      "league_unit_id",
      "league_unit_name",
      "countIf(played_minutes > 0)" as "games",
      "sum(played_minutes)" as "played",
      "sum(goals)" as "scored",
      "floor(played / scored, 2)" as "goal_rate",
      "argMax(nationality, round)" as "nationality",
      s"arrayFirst(x -> x != 0, topK(2)(${ClickhouseRequest.roleIdCase("role_id")}))" as "role",
      "((argMax(age, round) * 112) + argMax(days, round))" as "age"
    ).from("hattrick.player_stats")
      .where
      .season(parameters.season)
      .orderingKeyPath(orderingKeyPath)
      .isLeagueMatch
      .groupBy(
        "player_id", "first_name", "last_name", "team_id", "league_unit_id", "league_unit_name"
      ).having
      .round.lessEqual(round)
      .role(role.map(Roles.reverseMapping))
      .nationality(playersParameters.nationality)
      .age.greaterEqual(playersParameters.minAge.map(_ * 112))
      .age.lessEqual(playersParameters.maxAge.map(_ * 112 + 111))
      .orderBy(parameters.sortBy.to(parameters.sortingDirection.toSql),
        "player_id".to(parameters.sortingDirection.toSql))
      .limit(page = parameters.page, pageSize = parameters.pageSize)
      .setting("max_bytes_before_external_group_by", 1000000000L)
  }
}
