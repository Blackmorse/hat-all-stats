package com.blackmorse.hattid.web.databases.requests.model.team

import anorm.RowParser
import anorm.SqlParser.get
import anorm.~
import zio.json.{DeriveJsonEncoder, JsonEncoder}

import java.util.Date

case class OldestTeam(teamSortingKey: TeamSortingKey,
                      foundedDate: Date)

object OldestTeam {
  // Encoder for Date to Long (timestamp). TODO: check if play decoded to ms also
  implicit val dateEncoder: JsonEncoder[Date] = {
    JsonEncoder.long.contramap[Date](_.getTime)
    
  }
  implicit val jsonEncoder: JsonEncoder[OldestTeam] = DeriveJsonEncoder.gen[OldestTeam]

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
