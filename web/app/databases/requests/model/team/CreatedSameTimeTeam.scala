package databases.requests.model.team

import java.util.Date
import anorm.SqlParser.get
import anorm.~
import play.api.libs.json.Json

case class CreatedSameTimeTeam(teamSortingKey: TeamSortingKey,
                               foundedDate: Date,
                               powerRating: Int)

object CreatedSameTimeTeam {
  implicit val writes = Json.writes[CreatedSameTimeTeam]

  val createdSameTimeTeamMapper = {
    get[Int]("league_id") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Date]("founded_date") ~
    get[Int]("power_rating") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName
        ~ foundedDate ~ powerRating =>
        CreatedSameTimeTeam(
          teamSortingKey = TeamSortingKey(
            teamId = teamId,
            teamName = teamName,
            leagueUnitId = leagueUnitId,
            leagueUnitName = leagueUnitName,
            leagueId = leagueId
          ),
          foundedDate = foundedDate,
          powerRating = powerRating
        )
    }
  }
}
