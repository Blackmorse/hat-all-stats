package databases.requests.model.team

import anorm.RowParser
import anorm.SqlParser.get
import play.api.libs.json.{Json, OWrites}
import anorm.~

import java.util.Date

case class OldestTeam(teamSortingKey: TeamSortingKey,
                      foundedDate: Date)

object OldestTeam {
  implicit val writes: OWrites[OldestTeam] = Json.writes[OldestTeam]

  val mapper: RowParser[OldestTeam] = {
    get[Int]("league_id") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Date]("founded_date") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
         foundedDate =>
        OldestTeam(
          teamSortingKey = TeamSortingKey(
            teamId = teamId,
            teamName = teamName,
            leagueUnitId = leagueUnitId,
            leagueUnitName = leagueUnitName,
            leagueId = leagueId),
          foundedDate = foundedDate
        )
    }
  }
}
