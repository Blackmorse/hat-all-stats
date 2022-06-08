package databases.requests.playerstats.player

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.Roles
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.player.PlayerCards
import models.web.{PlayersParameters, RestStatisticsParameters}
import sqlbuilder.{Select, SqlBuilder}

object PlayerCardsRequest extends ClickhousePlayerRequest[PlayerCards] {
  override val sortingColumns: Seq[String] = Seq("games", "played", "yellow_cards", "red_cards")

  override val rowParser: RowParser[PlayerCards] = PlayerCards.mapper

  override def buildSql(orderingKeyPath: OrderingKeyPath,
                        parameters: RestStatisticsParameters,
                        playersParameters: PlayersParameters,
                        role: Option[String], round: Int): SqlBuilder = {
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
        "sum(yellow_cards)" as "yellow_cards",
        "sum(red_cards)" as "red_cards",
        "argMax(nationality, round)" as "nationality",
        s"arrayFirst(x -> x != 0, topK(2)(${ClickhouseRequest.roleIdCase("role_id")}))" as "role",
        "((argMax(age, round) * 112) + argMax(days, round))" as "age"
      ).from("hattrick.player_stats")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .isLeagueMatch
      .groupBy("player_id", "first_name", "last_name", "team_id", "league_unit_id", "league_unit_name")
      .having
        .round.lessEqual(round)
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
