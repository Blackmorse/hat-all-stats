package databases.requests.playerstats.team.chart

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.team.TeamRatingChart
import databases.requests.teamrankings.ClickhouseChartRequest
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import sqlbuilder.{Select, SqlBuilder}

object TeamRatingsChartRequest extends ClickhouseChartRequest[TeamRatingChart] {
  override val rowParser: RowParser[TeamRatingChart] = TeamRatingChart.mapper

  override def sqlBuilder(orderingKeyPath: OrderingKeyPath, season: Int): SqlBuilder = {
    import sqlbuilder.SqlBuilder.implicits.*
    val clauseEntry = Select(
      "any(league_id)" `as` "league",
      "argMax(team_name, round)" `as` "team_name",
      "team_id",
      "league_unit_id",
      "league_unit_name",
      "season",
      "round",
      "sum(rating)" `as` "rating",
      "sum(rating_end_of_match)" `as` "rating_end_of_match"
    ).from("hattrick.player_stats")
      .where
      .season(season)
      .orderingKeyPath(orderingKeyPath)
      .isLeagueMatch

    //fix applied from 3rd round of 89th season
    val finalClause = if (season <= 88) {
      clauseEntry
    } else {
      // ?? not sure what's the impact on the chart. Look at TeamRatingChart
      clauseEntry.startingLineup
    } 
    finalClause
      .groupBy("team_id", "league_unit_id", "league_unit_name", "season", "round")
  }
}
