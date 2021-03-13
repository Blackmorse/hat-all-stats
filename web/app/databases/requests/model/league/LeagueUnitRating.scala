package databases.requests.model.league

import anorm.SqlParser.get
import anorm.~
import play.api.libs.json.Json

case class LeagueUnitRating(leagueUnitId: Long, leagueUnitName: String,
                            hatStats: Int, midfield: Int, defense: Int, attack: Int,
                            loddarStats: Double)

object LeagueUnitRating {
  implicit val writes = Json.writes[LeagueUnitRating]

  val leagueUnitRatingMapper = {
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("hatstats") ~
    get[Int]("midfield") ~
    get[Int]("defense") ~
    get[Int]("attack") ~
    get[Double]("loddar_stats") map {
      case leagueUnitId ~ leagueUnitName ~ hatstats ~ midfield ~ defense ~ attack ~ loddarStats =>
        LeagueUnitRating(leagueUnitId = leagueUnitId,
          leagueUnitName = leagueUnitName,
          hatStats = hatstats,
          midfield = midfield,
          defense = defense,
          attack = attack,
          loddarStats = loddarStats)
    }
  }
}


