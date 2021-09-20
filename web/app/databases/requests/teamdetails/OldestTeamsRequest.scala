package databases.requests.teamdetails

import anorm.RowParser
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import databases.requests.model.team.OldestTeam
import databases.sqlbuilder.{Select, SqlBuilder}
import models.web.{RestStatisticsParameters, Round}

object OldestTeamsRequest extends ClickhouseStatisticsRequest[OldestTeam] {
  override val sortingColumns: Seq[String] = Seq("founded_date")

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
      "founded_date"
    ).from("hattrick.team_details")
      .where
      .season(parameters.season)
      .orderingKeyPath(orderingKeyPath)
      .round(parameters.statsType.asInstanceOf[Round].round)
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection.reverse),
        "team_id".to(parameters.sortingDirection.reverse)
      )
      .limit(page = parameters.page, pageSize = parameters.pageSize)
  }

  override def aggregateBuilder(orderingKeyPath: OrderingKeyPath, parameters: RestStatisticsParameters, aggregateFuntion: SqlBuilder.func): SqlBuilder =
    throw new UnsupportedOperationException("No aggregate allowed for OldestTeamsRequest")

  override val rowParser: RowParser[OldestTeam] = OldestTeam.mapper
}
