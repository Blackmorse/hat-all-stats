package databases.requests.model.team

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import play.api.libs.json.{Json, OWrites}

case class TeamPowerRating(teamSortingKey: TeamSortingKey,
                           powerRating: Int)

object TeamPowerRating {
  implicit val writes: OWrites[TeamPowerRating] = Json.writes[TeamPowerRating]

  val mapper: RowParser[TeamPowerRating] = {
    get[Int]("league_id") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("power_rating") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ powerRating =>
        val teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId)
        TeamPowerRating(teamSortingKey, powerRating)
    }
  }
}
