package databases.requests.matchdetails

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.league.LeagueUnitRating
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import databases.sql.Fields.{hatstats, loddarStats}
import models.web.RestStatisticsParameters
import sqlbuilder.functions.avg
import sqlbuilder.{NestedSelect, Select, SqlBuilder, functions}

object LeagueUnitHatstatsRequest extends ClickhouseStatisticsRequest[LeagueUnitRating] {
  override val sortingColumns: Seq[String] = Seq("hatstats", "midfield", "defense", "attack", "loddar_stats")

  override val rowParser: RowParser[LeagueUnitRating] = LeagueUnitRating.leagueUnitRatingMapper

  override def aggregateBuilder(orderingKeyPath: OrderingKeyPath,
                                parameters: RestStatisticsParameters,
                                aggregateFunction: functions.func): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
        "league_unit_id",
        "league_unit_name",
        aggregateFunction("hatstats").toInt32 `as` "hatstats",
        aggregateFunction("midfield").toInt32 `as` "midfield",
        aggregateFunction("defense").toInt32 `as` "defense",
        aggregateFunction("attack").toInt32 `as` "attack",
        aggregateFunction("loddar_stats") `as` "loddar_stats"
      ).from(
        NestedSelect(
            "league_unit_id",
            "league_unit_name",
            "round",
            avg(hatstats).toInt32 `as` "hatstats",
            avg("rating_midfield").toInt32 `as` "midfield",
            avg("rating_right_def + rating_left_def + rating_mid_def").toInt32 `as` "defense",
            avg("rating_right_att + rating_mid_att + rating_left_att").toInt32 `as` "attack",
            avg(loddarStats) `as` "loddar_stats"
          )
          .from("hattrick.match_details")
          .where
            .season(parameters.season)
            .orderingKeyPath(orderingKeyPath)
            .isLeagueMatch
            .and("rating_midfield + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att != 0")
          .groupBy("league_unit_id", "league_unit_name", "round")
      ).groupBy("league_unit_id", "league_unit_name")
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection.toSql),
        "league_unit_id".desc
      ).limit(page = parameters.page, pageSize = parameters.pageSize)
  }

  override def oneRoundBuilder(orderingKeyPath: OrderingKeyPath,
                               parameters: RestStatisticsParameters,
                               round: Int): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
        "league_unit_id",
        "league_unit_name",
        avg(hatstats).toInt32 `as` "hatstats",
        avg("rating_midfield").toInt32 `as` "midfield",
        avg("rating_right_def + rating_left_def + rating_mid_def").toInt32 `as` "defense",
        avg("rating_right_att + rating_mid_att + rating_left_att").toInt32 `as` "attack",
        avg(loddarStats) `as` "loddar_stats"
      ).from("hattrick.match_details")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .round(round)
        .isLeagueMatch
        .and("rating_midfield + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att != 0")
      .groupBy("league_unit_id, league_unit_name")
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection.toSql),
        "league_unit_id".to(parameters.sortingDirection.toSql)
      ).limit(page = parameters.page, pageSize = parameters.pageSize)
  }
}
