package com.blackmorse.hattid.web.databases.requests.overview

import anorm.RowParser
import com.blackmorse.hattid.web.databases.requests.ClickhouseOverviewRequest
import com.blackmorse.hattid.web.databases.requests.model.`match`.MatchTopHatstats
import com.blackmorse.hattid.web.databases.sql.Fields._
import sqlbuilder.{Select, SqlBuilder}

object SurprisingMatchesOverviewRequest extends ClickhouseOverviewRequest[MatchTopHatstats] {
  override val rowParser: RowParser[MatchTopHatstats] = MatchTopHatstats.mapper

  override def builder(season: Int,
                       round: Int,
                       leagueId: Option[Int],
                       divisionLevel: Option[Int]): SqlBuilder = {
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
      )
      .from("hattrick.match_details")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch
        .and("(((goals - enemy_goals) * hatstats_difference) < 0)")
        .and("opposite_team_id != 0")
      .orderBy(
        "abs_hatstats_difference".desc,
        "team_id".desc
      )
      .limitBy(1, "match_id")
      .limit(limit)
  }
}
