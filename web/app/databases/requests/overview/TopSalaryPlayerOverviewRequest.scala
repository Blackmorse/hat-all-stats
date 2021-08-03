package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.PlayerStatOverview
import databases.sqlbuilder.{Select, SqlBuilder}

object TopSalaryPlayerOverviewRequest extends ClickhouseOverviewRequest[PlayerStatOverview] {
  override val rowParser: RowParser[PlayerStatOverview] = PlayerStatOverview.mapper

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
        "player_id",
        "first_name",
        "last_name",
        "salary" as "value",
        "nationality"
      )
      .from("hattrick.player_stats")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch
      .orderBy("value".desc)
      .limit(limit)
  }
}
