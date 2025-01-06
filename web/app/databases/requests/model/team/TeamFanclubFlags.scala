package databases.requests.model.team

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import play.api.libs.json.{Json, OWrites}

case class TeamFanclubFlags(teamSortingKey: TeamSortingKey,
                            fanclubSize: Int,
                            homeFlags: Int,
                            awayFlags: Int,
                            allFlags: Int)

object TeamFanclubFlags {
  implicit val writes: OWrites[TeamFanclubFlags] = Json.writes[TeamFanclubFlags]

  val mapper: RowParser[TeamFanclubFlags] = {
    get[Int]("league_id") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("fanclub_size") ~
    get[Int]("home_flags") ~
    get[Int]("away_flags") ~
    get[Int]("all_flags") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
        fanclubSize ~ homeFlags ~ awayFlags ~ allFlags =>

        val teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId)
        TeamFanclubFlags(teamSortingKey, fanclubSize, homeFlags, awayFlags, allFlags)
    }

  }
}
