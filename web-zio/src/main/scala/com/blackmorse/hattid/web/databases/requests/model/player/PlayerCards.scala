package com.blackmorse.hattid.web.databases.requests.model.player

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import com.blackmorse.hattid.web.databases.requests.model.Roles
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class PlayerCards(playerSortingKey: PlayerSortingKey,
                       games: Int,
                       playedMinutes: Int,
                       yellowCards: Int,
                       redCards: Int,
                       role: String,
                       age: Int)

object PlayerCards {
  implicit val jsonEncoder: JsonEncoder[PlayerCards] = DeriveJsonEncoder.gen[PlayerCards]

  val mapper: RowParser[PlayerCards] = {
    get[Int]("league") ~
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("games") ~
    get[Int]("played") ~
    get[Int]("yellow_cards") ~
    get[Int]("red_cards") ~
    get[Int]("nationality") ~
    get[Int]("role" ) ~
    get[Int]("age") map {
      case leagueId ~ playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ games ~
          playedMinutes ~ yellowCards ~ redCards ~ nationality ~ role ~ age =>
        val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
          leagueUnitId, leagueUnitName, nationality, leagueId)
        PlayerCards(playerSortingKey, games, playedMinutes, yellowCards, redCards, Roles.mapping(role), age)
    }
  }
}
