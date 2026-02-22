package com.blackmorse.hattid.web.databases.requests.model.overview

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import com.blackmorse.hattid.web.databases.requests.model.player.PlayerSortingKey
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class PlayerStatOverview(leagueId: Int,
                              playerSortingKey: PlayerSortingKey,
                              value: Int)

object PlayerStatOverview {
  implicit val jsonEncoder: JsonEncoder[PlayerStatOverview] = DeriveJsonEncoder.gen[PlayerStatOverview]

  val mapper: RowParser[PlayerStatOverview] = {
    get[Int]("league_id") ~
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("nationality") ~
    get[Int]("value") map {
      case leagueId ~ playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ nationality ~ value =>

        val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
          leagueUnitId, leagueUnitName, nationality, leagueId)
        PlayerStatOverview(leagueId, playerSortingKey, value)
    }
  }
}
