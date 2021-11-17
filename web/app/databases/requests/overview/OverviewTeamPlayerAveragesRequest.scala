package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.model.overview.OverviewTeamPlayerAverages
import databases.sqlbuilder.{NestedSelect, Select, SqlBuilder}

object OverviewTeamPlayerAveragesRequest extends ClickhouseOverviewRequest[OverviewTeamPlayerAverages] {
  override val rowParser: RowParser[OverviewTeamPlayerAverages] = OverviewTeamPlayerAverages.mapper

  override def builder(season: Int,
                       round: Int,
                       leagueId: Option[Int],
                       divisionLevel: Option[Int]): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
        "toUInt16(avg(avg_age))" as "avg_age",
        "toUInt32(avg(sum_salary))" as "avg_salary",
        "avg(sum_rating)" as "avg_rating"
      ).from(
      NestedSelect(
          "avg((age * 112) + days)" as "avg_age",
          "sum(rating)" as "sum_rating",
          "sum(salary)" as "sum_salary"
        ).from("hattrick.player_stats")
        .where
          .round(round)
          .season(season)
          .leagueId(leagueId)
          .divisionLevel(divisionLevel)
          .isLeagueMatch
          .limit(page = 0, pageSize = limit)
        .groupBy("team_id")
    )
  }
}
