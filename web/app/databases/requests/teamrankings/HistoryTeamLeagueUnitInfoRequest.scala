package databases.requests.teamrankings

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest
import databases.requests.ClickhouseRequest.*
import models.clickhouse.HistoryTeamLeagueUnitInfo
import sqlbuilder.Select
import zio.ZIO

object HistoryTeamLeagueUnitInfoRequest extends ClickhouseRequest[HistoryTeamLeagueUnitInfo] {
  override val rowParser: RowParser[HistoryTeamLeagueUnitInfo] = HistoryTeamLeagueUnitInfo.historyTeamLeagueUnitInfoMapper

  private def buildRequest(season: Int, leagueId: Int, teamId: Long) = {
    import sqlbuilder.SqlBuilder.implicits.*

    Select(
      "division_level",
      "league_unit_id"
    ).from("hattrick.team_rankings")
      .where
      .season(season)
      .leagueId(leagueId)
      .teamId(teamId)
      .limit(1)
  }

  def execute(season: Int, leagueId: Int, teamId: Long): DBIO[Option[HistoryTeamLeagueUnitInfo]] = wrapErrorsOpt {
    for {
      restClickhouseDAO <- ZIO.service[RestClickhouseDAO]
      res <- restClickhouseDAO.executeSingleOptZIO(buildRequest(season, leagueId, teamId).sqlWithParameters().build, rowParser)
    } yield res
  }
}
