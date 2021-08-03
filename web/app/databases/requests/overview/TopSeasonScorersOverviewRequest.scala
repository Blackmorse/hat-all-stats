package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.PlayerStatOverview
import databases.sqlbuilder.{NestedSelect, Select, SqlBuilder}

object TopSeasonScorersOverviewRequest extends ClickhouseOverviewRequest[PlayerStatOverview] {
  override val rowParser: RowParser[PlayerStatOverview] = PlayerStatOverview.mapper

  override def builder(season: Int,
                       round: Int,
                       leagueId: Option[Int],
                       divisionLevel: Option[Int]): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
        "any(league_id)" as "league_id",
        "player_id",
        "first_name",
        "last_name",
        "team_id",
        "argMax(team_name, round)" as "team_name",
        "league_unit_id",
        "league_unit_name",
        "sum(goals)" as "value",
        "argMax(nationality, round) as nationality"
      ).from(
        NestedSelect(
            "league_id",
            "player_id",
            "first_name",
            "last_name",
            "team_id",
            "team_name",
            "league_unit_id",
            "league_unit_name",
            "goals",
            "round",
            "nationality"
          ).from("hattrick.player_stats")
          .where
            .season(season)
            .leagueId(leagueId)
            .divisionLevel(divisionLevel)
            .round.lessEqual(round)
            .isLeagueMatch
      ).groupBy(
        "player_id",
        "first_name",
        "last_name",
        "team_id",
        "league_unit_id",
        "league_unit_name"
    ).orderBy("value".desc)
      .limit(pageSize = 5, page = 0)
  }
}
