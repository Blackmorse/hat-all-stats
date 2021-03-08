package models.clickhouse

import anorm.SqlParser.get
import anorm.~
import databases.requests.model.team.TeamSortingKey
import play.api.libs.json.Json

case class TeamHatstats(teamSortingKey: TeamSortingKey,
                        hatStats: Int, midfield: Int, defense: Int, attack: Int)

object TeamHatstats {
  implicit val writes = Json.writes[TeamHatstats]

  val teamRatingMapper = {
      get[Int]("league") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("hatstats") ~
      get[Int]("midfield") ~
      get[Int]("defense") ~
      get[Int]("attack") map {
        case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ hatstats ~ midfield ~ defense ~ attack =>
        TeamHatstats(
          TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId),
          hatstats,
          midfield,
          defense,
          attack)
    }
  }
}