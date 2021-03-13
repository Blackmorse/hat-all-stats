package databases.requests.model.team

import anorm.SqlParser.get
import anorm.~
import play.api.libs.json.Json

case class TeamHatstats(teamSortingKey: TeamSortingKey,
                        hatStats: Int, midfield: Int, defense: Int, attack: Int,
                        loddarStats: Double)

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
      get[Int]("attack") ~
      get[Double]("loddar_stats") map {
        case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ hatstats ~ midfield ~ defense ~ attack ~ loddarStats =>
        TeamHatstats(
          teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId),
          hatStats = hatstats,
          midfield = midfield,
          defense = defense,
          attack = attack,
          loddarStats = loddarStats)
    }
  }
}