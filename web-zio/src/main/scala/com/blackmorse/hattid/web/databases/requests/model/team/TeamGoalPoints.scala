package com.blackmorse.hattid.web.databases.requests.model.team

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class TeamGoalPoints(sortingKey: TeamSortingKey,
                          won: Int,
                          lost: Int,
                          draw: Int,
                          goalsFor: Int,
                          goalsAgaints: Int,
                          goalsDifference: Int,
                          points: Int)

object TeamGoalPoints {
  implicit val jsonEncoder: JsonEncoder[TeamGoalPoints] = DeriveJsonEncoder.gen[TeamGoalPoints]

  val mapper: RowParser[TeamGoalPoints] = {
    get[Int]("league") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("won") ~
    get[Int]("lost") ~
    get[Int]("draw") ~
    get[Int]("goals_for") ~
    get[Int]("goals_against") ~
    get[Int]("goals_difference") ~
    get[Int]("points") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
        won ~ lost ~ draw ~ goalsFor ~ goalsAgainst ~
        goalsDifference ~ points =>
        val teamSortingKey = TeamSortingKey(teamId = teamId,
          teamName = teamName,
          leagueUnitId = leagueUnitId,
          leagueUnitName = leagueUnitName,
          leagueId = leagueId)

        TeamGoalPoints(sortingKey = teamSortingKey,
          won = won,
          lost = lost,
          draw = draw,
          goalsFor = goalsFor,
          goalsAgaints = goalsAgainst,
          goalsDifference = goalsDifference,
          points = points)
    }
  }
}
