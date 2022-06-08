package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.model.`match`.MatchTopHatstats
import databases.sql.Fields._
import sqlbuilder.{Select, SqlBuilder}

object TopMatchesOverviewRequest extends ClickhouseOverviewRequest[MatchTopHatstats] {
  override val rowParser: RowParser[MatchTopHatstats] = MatchTopHatstats.mapper

  override def builder(season: Int,
                       round: Int,
                       leagueId: Option[Int],
                       divisionLevel: Option[Int]): SqlBuilder = {
    import SqlBuilder.implicits._
    Select("league_id",
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
        hatstats as "hatstats",
        oppositeHatstats as "opposite_hatstats",
        "hatstats + opposite_hatstats" as "sum_hatstats",
        loddarStats as "loddar_stats",
        oppositeLoddarStats as "opposite_loddar_stats",
        "loddar_stats + opposite_loddar_stats" as "sum_loddar_stats"
      )
      .from("hattrick.match_details")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch
      .orderBy(
        "sum_hatstats".desc,
        "team_id".desc
      )
      .limitBy(1, "match_id")
      .limit(limit)
  }
}
