package com.blackmorse.hattid.web.databases.requests.matchdetails

import anorm.RowParser
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import com.blackmorse.hattid.web.databases.requests.model.`match`.MatchTopHatstats
import com.blackmorse.hattid.web.databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import com.blackmorse.hattid.web.databases.sql.Fields._
import com.blackmorse.hattid.web.models.web.RestStatisticsParameters
import sqlbuilder.{Select, SqlBuilder, functions}

object MatchTopHatstatsRequest extends ClickhouseStatisticsRequest[MatchTopHatstats] {
  override val sortingColumns: Seq[String] = Seq("sum_hatstats", "sum_loddar_stats")

  override val rowParser: RowParser[MatchTopHatstats] = MatchTopHatstats.mapper

  override def aggregateBuilder(orderingKeyPath: OrderingKeyPath,
                                parameters: RestStatisticsParameters,
                                aggregateFunction: functions.func): SqlBuilder = {
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
        "goals",
        "enemy_goals",
        hatstats `as` "hatstats",
        oppositeHatstats `as` "opposite_hatstats",
        "hatstats + opposite_hatstats" `as` "sum_hatstats",
        loddarStats `as` "loddar_stats",
        oppositeLoddarStats `as` "opposite_loddar_stats",
        "loddar_stats + opposite_loddar_stats" `as` "sum_loddar_stats"
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
        "goals",
        "enemy_goals",
        hatstats `as` "hatstats",
        oppositeHatstats `as` "opposite_hatstats",
        "hatstats + opposite_hatstats" `as` "sum_hatstats",
        loddarStats `as` "loddar_stats",
        oppositeLoddarStats `as` "opposite_loddar_stats",
        "loddar_stats + opposite_loddar_stats" `as` "sum_loddar_stats"
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
}
