package databases.requests.model.team

import play.api.libs.json.Json
import anorm.SqlParser.get
import anorm.~

case class TeamStreakTrophies(teamSortingKey: TeamSortingKey,
                              trophiesNumber: Int,
                              numberOfVictories: Int,
                              numberOfUndefeated: Int)

object TeamStreakTrophies {
  implicit val writes = Json.writes[TeamStreakTrophies]

  val mapper = {
    get[Int]("league_id") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("trophies_number") ~
    get[Int]("number_of_victories") ~
    get[Int]("number_of_undefeated") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
        trophiesNumber ~ numberOfVictories ~ numberOfUndefeated =>
        val teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId)

        TeamStreakTrophies(teamSortingKey, trophiesNumber, numberOfVictories, numberOfUndefeated)
    }
  }
}
