package databases.requests.model.overview

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import databases.requests.model.team.TeamSortingKey
import play.api.libs.json.{Json, OWrites}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class TeamStatOverview(leagueId: Int, teamSortingKey: TeamSortingKey,
                            value: Int)

object TeamStatOverview {
  implicit val writes: OWrites[TeamStatOverview] = Json.writes[TeamStatOverview]
  implicit val jsonEncoder: JsonEncoder[TeamStatOverview] = DeriveJsonEncoder.gen[TeamStatOverview]

  val mapper: RowParser[TeamStatOverview] = {
    get[Int]("league_id") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("value") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ value =>
        val teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId)
        TeamStatOverview(leagueId, teamSortingKey, value)
    }
  }
}
