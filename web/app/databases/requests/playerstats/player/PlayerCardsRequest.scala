package databases.requests.playerstats.player

import anorm.RowParser
import databases.requests.ClickhouseStatisticsRequest
import databases.requests.model.player.PlayerCards

object PlayerCardsRequest extends ClickhouseStatisticsRequest[PlayerCards] {
  override val sortingColumns: Seq[String] = Seq("games", "played", "yellow_cards", "red_cards")
  override val aggregateSql: String = ""

  override val oneRoundSql: String = """
       |SELECT
       |    player_id,
       |    first_name,
       |    last_name,
       |    team_id,
       |    argMax(team_name, round) as team_name,
       |    league_unit_id,
       |    league_unit_name,
       |    countIf(played_minutes > 0) AS games,
       |    sum(played_minutes) AS played,
       |    sum(yellow_cards) AS yellow_cards,
       |    sum(red_cards) AS red_cards,
       |    argMax(nationality, round) as nationality
       |FROM hattrick.player_stats
       |__where__ AND round <= __round__
       |GROUP BY
       |    player_id,
       |    first_name,
       |    last_name,
       |    team_id,
       |    league_unit_id,
       |    league_unit_name
       |ORDER BY __sortBy__ __sortingDirection__, player_id __sortingDirection__
       |__limit__""".stripMargin

  override val rowParser: RowParser[PlayerCards] = PlayerCards.mapper
}
