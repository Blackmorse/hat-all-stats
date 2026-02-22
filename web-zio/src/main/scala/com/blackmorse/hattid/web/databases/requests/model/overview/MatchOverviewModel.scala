package com.blackmorse.hattid.web.databases.requests.model.overview

import java.util.Date
import anorm.SqlParser.get
import anorm.{RowParser, ~}

case class MatchOverviewModel(leagueId: Int, leagueUnitId: Int, leagueUnitName: String,
                              teamId: Long, teamName: String, oppositeTeamId: Long, oppositeTeamName: String,
                              goals: Int, enemyGoals: Int, matchId: Long, isHomeMatch: Boolean,
                              date: Date, value: Int, oppositeValue: Int)

object MatchOverviewModel {
  val mapper: RowParser[MatchOverviewModel] = {
    get[Int]("league_id") ~
    get[Int]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("opposite_team_id") ~
    get[String]("opposite_team_name") ~
    get[Int]("goals") ~
    get[Int]("enemy_goals") ~
    get[Long]("match_id") ~
    get[String]("is_home_match") ~
    get[Date]("dt") ~
    get[Int]("value") ~
    get[Int]("opposite_value") map {
      case leagueId ~ leagueUnitId ~ leagueUnitName ~ teamId ~ teamName ~ oppositeTeamId ~ oppositeTeamName ~
        goals ~ enemyGoals ~ matchId ~ isHomeMatch ~ dt ~ value ~ oppositeValue =>
        MatchOverviewModel(leagueId, leagueUnitId, leagueUnitName, teamId, teamName,
          oppositeTeamId, oppositeTeamName, goals, enemyGoals, matchId,
          if(isHomeMatch == "home") true else false, dt, value, oppositeValue)
    }
  }
}
