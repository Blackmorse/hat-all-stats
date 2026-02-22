package com.blackmorse.hattid.web.databases.requests.playerstats.team

import anorm.{Row, RowParser, SimpleSql}
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.*
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import com.blackmorse.hattid.web.databases.requests.model.team.TeamCards
import com.blackmorse.hattid.web.databases.requests.{ClickhouseRequest, ClickhouseStatisticsRequest, OrderingKeyPath}
import com.blackmorse.hattid.web.models.web.{RestStatisticsParameters, Round, SqlInjectionError}
import sqlbuilder.{Select, SqlBuilder}
import sqlbuilder.functions.*
import zio.ZIO

object TeamCardsRequest extends ClickhouseStatisticsRequest[TeamCards] {
  val sortingColumns: Seq[String] = Seq("yellow_cards", "red_cards")

  override val rowParser: RowParser[TeamCards] = TeamCards.mapper

  private def simpleSql(orderingKeyPath: OrderingKeyPath,
                        parameters: RestStatisticsParameters): SimpleSql[Row] = {
    val round = parameters.statsType match {
      case Round(r) => r
    }

    import sqlbuilder.SqlBuilder.implicits.*
    val builder = Select(
      any("league_id") `as` "league",
      argMax("team_name", "round") `as` "team_name",
      "team_id",
      "league_unit_id",
      "league_unit_name",
      sum("yellow_cards") `as` "yellow_cards",
      sum("red_cards") `as` "red_cards"
    ).from("hattrick.player_stats")
      .where
      .season(parameters.season)
      .orderingKeyPath(orderingKeyPath)
      .round.lessEqual(round)
      .groupBy("team_id", "league_unit_id", "league_unit_name")
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection.toSql),
        "team_id".asc
      ).limit(page = parameters.page, pageSize = parameters.pageSize)

    builder.sqlWithParameters().build
  }

  override def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters): DBIO[List[TeamCards]] = wrapErrors {
    if(!sortingColumns.contains(parameters.sortBy)) {
      ZIO.fail(SqlInjectionError())
    } else {
      for {
        restClickhouseDAO <- ZIO.service[RestClickhouseDAO]
        result <- restClickhouseDAO.executeZIO(simpleSql(orderingKeyPath, parameters), rowParser)
       } yield result
    }
  }

  // Not the best abstraction. But need an execute method for calling from routes
  override def oneRoundBuilder(orderingKeyPath: OrderingKeyPath, parameters: RestStatisticsParameters, round: Int): SqlBuilder = ???

  override def aggregateBuilder(orderingKeyPath: OrderingKeyPath, parameters: RestStatisticsParameters, aggregateFunction: func): SqlBuilder = ???
}
