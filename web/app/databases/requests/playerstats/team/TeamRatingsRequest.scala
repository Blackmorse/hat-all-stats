package databases.requests.playerstats.team

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import databases.requests.model.team.TeamRating
import models.web.RestStatisticsParameters
import sqlbuilder.{Select, SqlBuilder, functions}

object TeamRatingsRequest extends ClickhouseStatisticsRequest[TeamRating] {
  override val sortingColumns: Seq[String] = Seq("rating", "rating_end_of_match")

  override val rowParser: RowParser[TeamRating] = TeamRating.mapper

  override def oneRoundBuilder(orderingKeyPath: OrderingKeyPath,
                               parameters: RestStatisticsParameters,
                               round: Int): SqlBuilder = {
    import SqlBuilder.implicits._
    val clauseEntry = Select(
      "any(league_id)" `as` "league",
      "argMax(team_name, round)" `as` "team_name",
      "team_id",
      "league_unit_id",
      "league_unit_name",
      "sum(rating)" `as` "rating",
      "sum(rating_end_of_match)" `as` "rating_end_of_match"
    ).from("hattrick.player_stats")
      .where
      .season(parameters.season)
      .orderingKeyPath(orderingKeyPath)
      .round(round)
      .isLeagueMatch

    //fix applied from 3th round of 89th season
    val finalClause = if(parameters.season <= 88 ||
      (parameters.season == 89 && (round == 1 || round == 2))) {
      clauseEntry
    } else if (parameters.sortBy == "rating") {
      clauseEntry.startingLineup
    } else {
      clauseEntry.playedToTheEnd
    }

    finalClause
      .groupBy("team_id", "league_unit_id", "league_unit_name")
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection.toSql),
        "team_id".asc
      ).limit(page = parameters.page, pageSize = parameters.pageSize)
  }

  override def aggregateBuilder(orderingKeyPath: OrderingKeyPath,
                                parameters: RestStatisticsParameters,
                                aggregateFunction: functions.func): SqlBuilder =
    throw new UnsupportedOperationException("Aggregate is not allowed for TeamRatings")
}
