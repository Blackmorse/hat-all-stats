package com.blackmorse.hattid.web.databases.requests.model.player

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import com.blackmorse.hattid.web.databases.requests.model.Roles
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class PlayerGamesGoals(playerSortingKey: PlayerSortingKey,
                            games: Int,
                            playedMinutes: Int,
                            scored: Int,
                            goalRate: Double,
                            role: String,
                            age: Int)

object PlayerGamesGoals {
  implicit val jsonEncoder: JsonEncoder[PlayerGamesGoals] = DeriveJsonEncoder.gen[PlayerGamesGoals]

  val mapper: RowParser[PlayerGamesGoals] = {
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
    get[Int]("scored") ~
    get[Double]("goal_rate") ~
    get[Int]("nationality") ~
    get[Int]("role") ~
    get[Int]("age") map {
      case leagueId ~ playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ games ~ playedMinutes ~
        scored ~ goalRate ~ nationality ~ role ~ age =>
        val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
          leagueUnitId, leagueUnitName, nationality, leagueId)
        PlayerGamesGoals(
          playerSortingKey,
          games,
          playedMinutes,
          scored,
          if(goalRate.isNaN || goalRate.isInfinite) null.asInstanceOf[Double] else goalRate,
          Roles.mapping(role),
          age)
    }
  }
}


