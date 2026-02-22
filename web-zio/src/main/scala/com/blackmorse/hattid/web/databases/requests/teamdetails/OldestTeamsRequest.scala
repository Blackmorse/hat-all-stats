package com.blackmorse.hattid.web.databases.requests.teamdetails

import anorm.RowParser
import com.blackmorse.hattid.web.databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import com.blackmorse.hattid.web.databases.requests.model.team.OldestTeam
import com.blackmorse.hattid.web.models.web.{RestStatisticsParameters, Round}
import sqlbuilder.{Select, SqlBuilder, functions}

object OldestTeamsRequest extends ClickhouseStatisticsRequest[OldestTeam] {
  override val sortingColumns: Seq[String] = Seq("founded_date")

  override def oneRoundBuilder(orderingKeyPath: OrderingKeyPath,
                               parameters: RestStatisticsParameters,
                               round: Int): SqlBuilder = {
    import SqlBuilder.implicits._
    import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.implicits._
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
        parameters.sortBy.to(parameters.sortingDirection.reverse.toSql),
        "team_id".to(parameters.sortingDirection.reverse.toSql)
      )
      .limit(page = parameters.page, pageSize = parameters.pageSize)
  }

  override def aggregateBuilder(orderingKeyPath: OrderingKeyPath, parameters: RestStatisticsParameters, aggregateFunction: functions.func): SqlBuilder =
    throw new UnsupportedOperationException("No aggregate allowed for OldestTeamsRequest")

  override val rowParser: RowParser[OldestTeam] = OldestTeam.mapper
}
