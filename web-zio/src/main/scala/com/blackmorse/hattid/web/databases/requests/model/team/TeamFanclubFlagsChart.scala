package com.blackmorse.hattid.web.databases.requests.model.team

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import com.blackmorse.hattid.web.databases.requests.model.Chart
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class TeamFanclubFlagsChart(teamSortingKey: TeamSortingKey,
                                 season: Int,
                                 round: Int,
                                 fanclubSize: Int,
                                 homeFlags: Int,
                                 awayFlags: Int,
                                 allFlags: Int) extends Chart

object TeamFanclubFlagsChart {
  implicit val jsonEncoder: JsonEncoder[TeamFanclubFlagsChart] = DeriveJsonEncoder.gen[TeamFanclubFlagsChart]
  
  val mapper: RowParser[TeamFanclubFlagsChart] = {
    get[Int]("league_id") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("season") ~
      get[Int]("round") ~
      get[Int]("fanclub_size") ~
      get[Int]("home_flags") ~
      get[Int]("away_flags") ~
      get[Int]("all_flags") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ season ~ round ~
        fanclubSize ~ homeFlags ~ awayFlags ~ allFlags =>

        val teamSortingKey = TeamSortingKey(teamId = teamId,
          teamName = teamName,
          leagueUnitId = leagueUnitId,
          leagueUnitName = leagueUnitName,
          leagueId = leagueId)
        TeamFanclubFlagsChart(teamSortingKey = teamSortingKey,
          season = season,
          round = round,
          fanclubSize = fanclubSize,
          homeFlags = homeFlags,
          awayFlags = awayFlags,
          allFlags = allFlags)
    }

  }
}
