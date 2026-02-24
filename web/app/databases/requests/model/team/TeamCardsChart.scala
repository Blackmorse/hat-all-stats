package databases.requests.model.team

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import databases.requests.model.Chart
import play.api.libs.json.{Json, OWrites}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class TeamCardsChart(teamSortingKey: TeamSortingKey,
                          season: Int,
                          round: Int,
                          yellowCards: Int,
                          redCards: Int) extends Chart

object TeamCardsChart {
  implicit val writes: OWrites[TeamCardsChart] = Json.writes[TeamCardsChart]
  implicit val jsonEncoder: JsonEncoder[TeamCardsChart] = DeriveJsonEncoder.gen[TeamCardsChart]

  val mapper: RowParser[TeamCardsChart] = {
    get[Int]("league") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("season") ~
      get[Int]("round") ~
      get[Int]("yellow_cards_sum") ~
      get[Int]("red_cards_sum") map {
      case leagueId ~
        teamId ~
        teamName ~
        leagueUnitId ~
        leagueUnitName ~
        season ~
        round ~
        yellowCards ~
        redCards =>
        val teamSortingKey = TeamSortingKey(teamId = teamId,
          teamName = teamName,
          leagueUnitId = leagueUnitId,
          leagueUnitName = leagueUnitName,
          leagueId = leagueId)
        TeamCardsChart(teamSortingKey = teamSortingKey,
          season = season,
          round = round,
          yellowCards = yellowCards,
          redCards = redCards)
    }
  }
}

