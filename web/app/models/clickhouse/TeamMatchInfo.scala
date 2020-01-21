package models.clickhouse

import java.util.Date

import anorm.SqlParser.get
import anorm.~
import org.joda.time.DateTime

case class TeamMatchInfo(round: Int, matchId: Long, teamId: Long, teamName: String, hatStats: Int,
                         formation: String, dt: Date)

object TeamMatchInfo {
  val teamMatchInfoMapper = {
    get[Int]("round") ~
    get[Long]("match_id") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Int]("hatstats") ~
    get[String]("formation") ~
    get[DateTime]("dt") map {
      case round ~ matchId ~ teamId ~ teamName ~ hatstats ~ formation ~ dt =>
        TeamMatchInfo(round, matchId, teamId, teamName, hatstats, formation, dt.toDate)
    }
  }
}
