package databases.requests.teamrankings

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest
import databases.requests.ClickhouseRequest.implicits.SqlWithParametersExtended
import models.clickhouse.HistoryTeamLeagueUnitInfo
import sqlbuilder.Select

import scala.concurrent.Future

object HistoryTeamLeagueUnitInfoRequest extends ClickhouseRequest[HistoryTeamLeagueUnitInfo] {
  override val rowParser: RowParser[HistoryTeamLeagueUnitInfo] = HistoryTeamLeagueUnitInfo.historyTeamLeagueUnitInfoMapper

  def execute(season: Int, leagueId: Int, teamId: Long)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[Option[HistoryTeamLeagueUnitInfo]] = {
    import sqlbuilder.SqlBuilder.implicits._
    val builder = Select(
        "division_level",
        "league_unit_id"
      ).from("hattrick.team_rankings")
      .where
        .season(season)
        .leagueId(leagueId)
        .teamId(teamId)
      .limit(1)

    restClickhouseDAO.executeSingleOpt(builder.sqlWithParameters().build, rowParser)
  }
}
