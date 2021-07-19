package databases.requests.overview

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseOverviewRequest
import databases.requests.matchdetails.MatchTopHatstatsRequest
import databases.requests.model.`match`.MatchTopHatstats
import databases.requests.overview.TopHatstatsTeamOverviewRequest.rowParser
import databases.sqlbuilder.SqlBuilder

import scala.concurrent.Future

object TopMatchesOverviewRequest extends ClickhouseOverviewRequest[MatchTopHatstats] {
  override def sortBy: String = "sum_hatstats"

  override val sql: String = MatchTopHatstatsRequest.oneRoundSql

  override val rowParser: RowParser[MatchTopHatstats] = MatchTopHatstats.mapper

  override def execute(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int])
                      (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[MatchTopHatstats]] = {
    import SqlBuilder.implicits._
    import SqlBuilder.fields._
    val builder = new SqlBuilder("", newApi = true)
      .select("league_id",
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

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
