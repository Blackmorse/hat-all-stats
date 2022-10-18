package databases.requests.playerstats.team

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import databases.requests.model.team.TeamAgeInjury
import models.web.RestStatisticsParameters
import sqlbuilder.{Select, SqlBuilder, functions}

object TeamAgeInjuryRequest extends ClickhouseStatisticsRequest[TeamAgeInjury] {
  override val sortingColumns: Seq[String] = Seq("age", "injury", "injury_count")

  override val rowParser: RowParser[TeamAgeInjury] = TeamAgeInjury.mapper

  override def oneRoundBuilder(orderingKeyPath: OrderingKeyPath,
                               parameters: RestStatisticsParameters,
                               round: Int): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
        "any(league_id)" as "league",
        "argMax(team_name, round)" as "team_name",
        "team_id",
        "league_unit_id",
        "league_unit_name",
        "avg((age * 112) + days)".toInt32 as "age",
        "sumIf(injury_level, (played_minutes > 0) AND (injury_level > 0))" as "injury",
        "countIf(injury_level, (played_minutes > 0) AND (injury_level > 0))" as "injury_count"
      ).from("hattrick.player_stats")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .round(round)
        .isLeagueMatch
      .groupBy("team_id", "league_unit_id", "league_unit_name")
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection.toSql),
        "team_id".asc
      ).limit(page = parameters.page, pageSize = parameters.pageSize)
  }

  override def aggregateBuilder(orderingKeyPath: OrderingKeyPath,
                                parameters: RestStatisticsParameters,
                                aggregateFunction: functions.func): SqlBuilder =
    throw new UnsupportedOperationException("No aggregation for TeamAgeInjury")
}
