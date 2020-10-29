package models.clickhouse

import anorm.SqlParser.get
import anorm.~
import play.api.libs.json.Json

case class TeamHatstats(teamId: Long, teamName: String, leagueUnitId: Long, leagueUnitName: String,
                        hatStats: Int, midfield: Int, defense: Int, attack: Int)

object TeamHatstats {
  implicit val writes = Json.writes[TeamHatstats]

  val teamRatingMapper = {
    get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("hatstats") ~
      get[Int]("midfield") ~
      get[Int]("defense") ~
      get[Int]("attack") map {
      case teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ hatstats ~ midfield ~ defense ~ attack =>
        TeamHatstats(teamId, teamName, leagueUnitId, leagueUnitName, hatstats, midfield, defense, attack)
    }
  }
}