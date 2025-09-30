package databases.requests.model.team

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import play.api.libs.json.{Json, OWrites}

case class TeamCardsChart(teamSortingKey: TeamSortingKey,
                          round: Int,
                          yellowCards: Int,
                          redCards: Int)

object TeamCardsChart {
  implicit val writes: OWrites[TeamCardsChart] = Json.writes[TeamCardsChart]

  val mapper: RowParser[TeamCardsChart] = {
    get[Int]("league") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("round") ~
      get[Int]("yellow_cards") ~
      get[Int]("red_cards") map {
      case leagueId ~
        teamId ~
        teamName ~
        leagueUnitId ~
        leagueUnitName ~
        round ~
        yellowCards ~
        redCards =>
        val teamSortingKey = TeamSortingKey(teamId = teamId,
          teamName = teamName,
          leagueUnitId = leagueUnitId,
          leagueUnitName = leagueUnitName,
          leagueId = leagueId)
        TeamCardsChart(teamSortingKey = teamSortingKey,
          round = round,
          yellowCards = yellowCards,
          redCards = redCards)
    }
  }
}

