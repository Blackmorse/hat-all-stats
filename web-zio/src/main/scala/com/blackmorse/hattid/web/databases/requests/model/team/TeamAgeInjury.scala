package com.blackmorse.hattid.web.databases.requests.model.team

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class TeamAgeInjury(teamSortingKey: TeamSortingKey,
                         age: Int,
                         injury: Int,
                         injuryCount: Int)

object TeamAgeInjury {
  implicit val jsonEncoder: JsonEncoder[TeamAgeInjury] = DeriveJsonEncoder.gen[TeamAgeInjury]

  val mapper: RowParser[TeamAgeInjury] = {
    get[Int]("league") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("age") ~
    get[Int]("injury") ~
    get[Int]("injury_count") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
        age ~ injury ~ injuryCount =>
        val teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId)
        TeamAgeInjury(teamSortingKey, age, injury, injuryCount)
    }
  }
}
