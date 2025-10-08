package databases.requests.playerstats.team.chart

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.team.TeamAgeInjuryChart
import databases.requests.teamrankings.ClickhouseChartRequest
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import sqlbuilder.{Select, SqlBuilder}

object TeamAgeInjuryChartRequest extends ClickhouseChartRequest[TeamAgeInjuryChart] {

  override val rowParser: RowParser[TeamAgeInjuryChart] = TeamAgeInjuryChart.mapper

  override def sqlBuilder(orderingKeyPath: OrderingKeyPath, season: Int): SqlBuilder = {
    import sqlbuilder.SqlBuilder.implicits.*
    Select(
      "any(league_id)" `as` "league",
      "argMax(team_name, round)" `as` "team_name",
      "team_id",
      "league_unit_id",
      "league_unit_name",
      "season",
      "round",
      "avg((age * 112) + days)".toInt32 `as` "age",
      "sumIf(injury_level, (played_minutes > 0) AND (injury_level > 0))" `as` "injury",
      "countIf(injury_level, (played_minutes > 0) AND (injury_level > 0))" `as` "injury_count"
    ).from("hattrick.player_stats")
      .where
      .season(season)
      .orderingKeyPath(orderingKeyPath)
      .isLeagueMatch
      .groupBy("team_id", "league_unit_id", "league_unit_name", "season", "round")
  }
}
