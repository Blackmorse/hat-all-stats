package databases.requests.matchdetails

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.`match`.MatchSpectators
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import models.web.RestStatisticsParameters
import sqlbuilder.{Select, SqlBuilder, functions}

object MatchSpectatorsRequest extends ClickhouseStatisticsRequest[MatchSpectators] {
  override val sortingColumns: Seq[String] = Seq("sold_total")
  override val rowParser: RowParser[MatchSpectators] = MatchSpectators.mapper

  //TODO common Seq of fields

  override def oneRoundBuilder(orderingKeyPath: OrderingKeyPath,
                               parameters: RestStatisticsParameters,
                               round: Int): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
        "league_id",
        "league_unit_id",
        "league_unit_name",
        "team_id",
        "team_name",
        "opposite_team_id",
        "opposite_team_name",
        "match_id",
        "is_home_match",
        "sold_total",
        "goals",
        "enemy_goals"
      ).from("hattrick.match_details")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .isLeagueMatch
        .round(round)
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection.toSql),
        "team_id".to(parameters.sortingDirection.toSql)
      ).limitBy(1, "match_id")
      .limit(page = parameters.page, pageSize = parameters.pageSize)
  }

  override def aggregateBuilder(orderingKeyPath: OrderingKeyPath,
                                parameters: RestStatisticsParameters,
                                aggregateFuntion: functions.func): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
        "league_id",
        "league_unit_id",
        "league_unit_name",
        "team_id",
        "team_name",
        "opposite_team_id",
        "opposite_team_name",
        "match_id",
        "is_home_match",
        "sold_total",
        "goals",
        "enemy_goals"
      ).from("hattrick.match_details")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .isLeagueMatch
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection.toSql),
        "team_id".to(parameters.sortingDirection.toSql)
      ).limitBy(1, "match_id")
      .limit(page = parameters.page, pageSize = parameters.pageSize)
  }
}
