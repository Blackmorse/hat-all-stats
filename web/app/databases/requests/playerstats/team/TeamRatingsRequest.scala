package databases.requests.playerstats.team

import anorm.RowParser
import databases.requests.ClickhouseStatisticsRequest
import databases.requests.model.team.TeamRating

object TeamRatingsRequest extends ClickhouseStatisticsRequest[TeamRating] {
  override val sortingColumns: Seq[String] = Seq("rating", "rating_end_of_match")

  override val aggregateSql: String = ""

  override val oneRoundSql: String = """
     |SELECT
     |    any(league_id) as league,
     |    argMax(team_name, round) as team_name,
     |    team_id,
     |    league_unit_id,
     |    league_unit_name,
     |    sum(rating) AS rating,
     |    sum(rating_end_of_match) AS rating_end_of_match
     |FROM hattrick.player_stats
     |__where__ AND (round = __round__)
     |GROUP BY
     |    team_id,
     |    league_unit_id,
     |    league_unit_name
     |ORDER BY __sortBy__ __sortingDirection__, team_id asc
     |__limit__""".stripMargin

  override val rowParser: RowParser[TeamRating] = TeamRating.mapper
}
