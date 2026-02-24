package com.blackmorse.hattid.web.models.clickhouse

import anorm.SqlParser.get
import anorm.~
import org.joda.time.DateTime

import java.util.Date

case class TeamMatchInfo(round: Int, matchId: Long, teamId: Long, teamName: String,
                         ratingMidfield: Int, ratingRightDef: Int, ratingLeftDef: Int,
                         ratingMidDef: Int, ratingRightAtt: Int, ratingLeftAtt: Int, ratingMidAtt: Int,
                         formation: String, dt: Date)

object TeamMatchInfo {
  val teamMatchInfoMapper = {
    get[Int]("round") ~
    get[Long]("match_id") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Int]("rating_midfield") ~
    get[Int]("rating_right_def") ~
    get[Int]("rating_left_def") ~
    get[Int]("rating_mid_def") ~
    get[Int]("rating_right_att") ~
    get[Int]("rating_left_att") ~
    get[Int]("rating_mid_att") ~
    get[String]("formation") ~
    get[DateTime]("dt") map {
      case round ~ matchId ~ teamId ~ teamName ~
        ratingMidfield ~ ratingRightDef ~ ratingLeftDef ~ ratingMidDef ~
        ratingRightAtt ~ ratingLeftAtt ~ ratingMidAtt ~
        formation ~ dt =>
        TeamMatchInfo(round, matchId, teamId, teamName,
          ratingMidfield,  ratingRightDef, ratingLeftDef, ratingMidDef,
          ratingRightAtt, ratingLeftAtt, ratingMidAtt,
          formation, dt.toDate)
    }
  }
}
