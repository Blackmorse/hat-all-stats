package databases.requests.teamrankings

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest
import databases.requests.ClickhouseRequest.*
import models.clickhouse.HistoryTeamLeagueUnitInfo
import models.web.HattidError
import sqlbuilder.Select
import zio.IO

import scala.concurrent.Future

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

  def execute(season: Int, leagueId: Int, teamId: Long)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[Option[HistoryTeamLeagueUnitInfo]] =
    restClickhouseDAO.executeSingleOpt(buildRequest(season, leagueId, teamId).sqlWithParameters().build, rowParser)

  def executeZIO(season: Int, leagueId: Int, teamId: Long)
             (implicit restClickhouseDAO: RestClickhouseDAO): IO[HattidError, Option[HistoryTeamLeagueUnitInfo]] = {
    restClickhouseDAO.executeSingleOptZIO(buildRequest(season, leagueId, teamId).sqlWithParameters().build, rowParser)
      .hattidErrors
  }
}
