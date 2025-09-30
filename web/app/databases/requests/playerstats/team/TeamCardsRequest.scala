package databases.requests.playerstats.team

import anorm.{Row, RowParser, SimpleSql}
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.*
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.team.TeamCards
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import models.web.{RestStatisticsParameters, Round, SqlInjectionError}
import sqlbuilder.Select
import sqlbuilder.functions.*
import zio.ZIO

object TeamCardsRequest extends ClickhouseRequest[TeamCards] {
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

  def execute(orderingKeyPath: OrderingKeyPath,
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
}
