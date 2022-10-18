package databases.requests.teamdetails

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.team.TeamPowerRating
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import models.web.{RestStatisticsParameters, Round}
import sqlbuilder.{Select, SqlBuilder, functions}

object TeamPowerRatingsRequest extends ClickhouseStatisticsRequest[TeamPowerRating] {
  override val sortingColumns: Seq[String] = Seq("power_rating")
  override val rowParser: RowParser[TeamPowerRating] = TeamPowerRating.mapper

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
        "power_rating"
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
    throw new UnsupportedOperationException("No aggregation allowed for TeamPowerRatings")
}
