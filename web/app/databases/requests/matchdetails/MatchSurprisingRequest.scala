package databases.requests.matchdetails

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.`match`.MatchTopHatstats
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import databases.sql.Fields.{hatstats, loddarStats, oppositeHatstats, oppositeLoddarStats}
import models.web.RestStatisticsParameters
import sqlbuilder.{Select, SqlBuilder, functions}

//TODO the same SQL Builders for oneRound and aggregate ?
object MatchSurprisingRequest extends ClickhouseStatisticsRequest[MatchTopHatstats] {
  override val sortingColumns: Seq[String] = Seq("abs_goals_difference",
    "abs_hatstats_difference", "abs_loddar_stats_difference")

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
        "abs(goals - enemy_goals)" `as` "abs_goals_difference",
        hatstats `as` "hatstats",
        oppositeHatstats `as` "opposite_hatstats",
        "hatstats - opposite_hatstats" `as` "hatstats_difference",
        "abs(hatstats_difference)" `as` "abs_hatstats_difference",
        loddarStats `as` "loddar_stats",
        oppositeLoddarStats `as` "opposite_loddar_stats",
        "abs(loddar_stats - opposite_loddar_stats)" `as` "abs_loddar_stats_difference"
      ).from("hattrick.match_details")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .isLeagueMatch
        .and("(((goals - enemy_goals) * hatstats_difference) < 0) AND (opposite_team_id != 0)")
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
        "abs(goals - enemy_goals)" `as` "abs_goals_difference",
        hatstats `as` "hatstats",
        oppositeHatstats `as` "opposite_hatstats",
        "hatstats - opposite_hatstats" `as` "hatstats_difference",
        "abs(hatstats_difference)" `as` "abs_hatstats_difference",
        loddarStats `as` "loddar_stats",
        oppositeLoddarStats `as` "opposite_loddar_stats",
        "abs(loddar_stats - opposite_loddar_stats)" `as` "abs_loddar_stats_difference"
      ).from("hattrick.match_details")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .isLeagueMatch
        .round(round)
        .and("(((goals - enemy_goals) * hatstats_difference) < 0)  AND (opposite_team_id != 0)"
      ).orderBy(
        parameters.sortBy.to(parameters.sortingDirection.toSql),
        "team_id".to(parameters.sortingDirection.toSql)
      ).limitBy(1, "match_id")
      .limit(page = parameters.page, pageSize = parameters.pageSize)
  }
}
