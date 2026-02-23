package com.blackmorse.hattid.web.databases.requests.teamrankings

import anorm.RowParser
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.*
import com.blackmorse.hattid.web.models.clickhouse.HistoryTeamLeagueUnitInfo
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
    RestClickhouseDAO.executeSingleOptZIO(buildRequest(season, leagueId, teamId).sqlWithParameters().build, rowParser)
  }
}
