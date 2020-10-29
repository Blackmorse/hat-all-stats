package databases.requests.model.team

import anorm.SqlParser.get
import anorm.~
import play.api.libs.json.Json

case class TeamFanclubFlags(teamSortingKey: TeamSortingKey,
                            fanclubSize: Int,
                            homeFlags: Int,
                            awayFlags: Int,
                            allFlags: Int)

object TeamFanclubFlags {
  implicit val writes = Json.writes[TeamFanclubFlags]

  val mapper = {
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("fanclub_size") ~
    get[Int]("home_flags") ~
    get[Int]("away_flags") ~
    get[Int]("all_flags") map {
      case teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
        fanclubSize ~ homeFlags ~ awayFlags ~ allFlags =>

        val teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName)
        TeamFanclubFlags(teamSortingKey, fanclubSize, homeFlags, awayFlags, allFlags)
    }

  }
}
