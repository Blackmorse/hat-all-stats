package databases.requests.teamdetails

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import databases.requests.model.team.TeamStreakTrophies
import models.web.{Asc, Desc, RestStatisticsParameters, Round, SortingDirection}
import sqlbuilder.{Select, SqlBuilder, SqlSortingDirection, functions}

object TeamStreakTrophiesRequest extends ClickhouseStatisticsRequest[TeamStreakTrophies] {
  override val sortingColumns: Seq[String] = Seq("trophies_number", "number_of_victories", "number_of_undefeated")

  override val rowParser: RowParser[TeamStreakTrophies] = TeamStreakTrophies.mapper

  override def oneRoundBuilder(orderingKeyPath: OrderingKeyPath,
                               parameters: RestStatisticsParameters,
                               round: Int): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
        "league_id",
        "team_id",
        "team_name",
        "league_unit_id",
        "league_unit_name",
        "trophies_number",
        "number_of_victories",
        "number_of_undefeated"
      )
      .from("hattrick.team_details")
      .where
      .season(parameters.season)
      .orderingKeyPath(orderingKeyPath)
      .round(parameters.statsType.asInstanceOf[Round].round)
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection.toSql),
        "team_id".to(parameters.sortingDirection.toSql))
      .limit(page = parameters.page, pageSize = parameters.pageSize)
  }

  override def aggregateBuilder(orderingKeyPath: OrderingKeyPath,
                                parameters: RestStatisticsParameters,
                                aggregateFunction: functions.func): SqlBuilder =
    throw new UnsupportedOperationException("No aggregation allowed for TeamStreakTrophies")
}
