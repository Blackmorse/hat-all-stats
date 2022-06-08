package databases.requests.playerstats.player

import anorm.RowParser
import databases.requests.model.player.PlayerInjury
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import models.web.RestStatisticsParameters
import sqlbuilder.{Select, SqlBuilder, functions}
import databases.requests.ClickhouseRequest

object PlayerInjuryRequest extends ClickhouseStatisticsRequest[PlayerInjury] {
  override val sortingColumns: Seq[String] = Seq("age", "injury")

  override val rowParser: RowParser[PlayerInjury] = PlayerInjury.mapper

  override def oneRoundBuilder(orderingKeyPath: OrderingKeyPath,
                               parameters: RestStatisticsParameters,
                               round: Int): SqlBuilder = {
    import SqlBuilder.implicits._
    import ClickhouseRequest.implicits._
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
        "injury_level" as "injury",
        "nationality"
      ).from("hattrick.player_stats")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .round(round)
        .isLeagueMatch
      .orderBy(parameters.sortBy.to(parameters.sortingDirection.toSql),
        "player_id".to(parameters.sortingDirection.toSql))
      .limit(page = parameters.page, pageSize = parameters.pageSize)
  }

  override def aggregateBuilder(orderingKeyPath: OrderingKeyPath,
                                parameters: RestStatisticsParameters,
                                aggregateFunction: functions.func): SqlBuilder =
    throw new UnsupportedOperationException("No aggregation allowed for PlayerInjuryRequest")
}
