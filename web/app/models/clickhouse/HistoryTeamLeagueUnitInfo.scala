package models.clickhouse
import anorm.SqlParser.get
import anorm.~

case class HistoryTeamLeagueUnitInfo(divisionLevel: Int, leagueUnitId: Long)

object HistoryTeamLeagueUnitInfo {
  val historyTeamLeagueUnitInfoMapper = {
    get[Int]("division_level") ~
    get[Long]("league_unit_id") map {
      case divisionLevel ~ leagueUnitId => HistoryTeamLeagueUnitInfo(divisionLevel, leagueUnitId)
    }
  }
}