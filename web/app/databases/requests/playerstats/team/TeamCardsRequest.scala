package databases.requests.playerstats.team

import anorm.RowParser
import databases.requests.ClickhouseStatisticsRequest
import databases.requests.model.team.TeamCards

object TeamCardsRequest extends ClickhouseStatisticsRequest[TeamCards] {
  override val sortingColumns: Seq[String] = Seq("yellow_cards", "red_cards")
  override val aggregateSql: String = ""
  override val oneRoundSql: String =
    """
      |SELECT
      |    argMax(team_name, round) as team_name,
      |    team_id,
      |    league_unit_id,
      |    league_unit_name,
      |    sum(yellow_cards) as yellow_cards,
      |    sum(red_cards) as red_cards
      |FROM hattrick.player_stats
      |__where__ AND (round <= __round__)
      |GROUP BY
      |    team_id,
      |    league_unit_id,
      |    league_unit_name
      |ORDER BY __sortBy__ __sortingDirection__, team_id asc
      |__limit__
      |""".stripMargin
  override val rowParser: RowParser[TeamCards] = TeamCards.mapper
}
