package databases.requests.overview.model

import databases.requests.model.team.TeamSortingKey
import play.api.libs.json.Json
import anorm.SqlParser.get
import anorm.~

case class TeamStatOverview(leagueId: Int, teamSortingKey: TeamSortingKey,
                            value: Int)

object TeamStatOverview {
  implicit val writes = Json.writes[TeamStatOverview]

  val mapper = {
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
