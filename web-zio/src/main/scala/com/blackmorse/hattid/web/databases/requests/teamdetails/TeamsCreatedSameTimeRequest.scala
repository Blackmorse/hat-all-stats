package com.blackmorse.hattid.web.databases.requests.teamdetails

import anorm.RowParser
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.DBIO
import com.blackmorse.hattid.web.databases.requests.model.team.CreatedSameTimeTeam
import com.blackmorse.hattid.web.service.DatesRange
import sqlbuilder.Select
import zio.ZIO

object TeamsCreatedSameTimeRequest extends ClickhouseRequest[CreatedSameTimeTeam] {
  override val rowParser: RowParser[CreatedSameTimeTeam] = CreatedSameTimeTeam.createdSameTimeTeamMapper

  def execute(leagueId: Int, currentSeason: Int, currentRound: Int, datesRange: DatesRange): DBIO[List[CreatedSameTimeTeam]] = wrapErrors {
    import sqlbuilder.SqlBuilder.implicits.*
    val builder = Select(
        "league_id",
        "team_id",
        "team_name",
        "league_unit_id",
        "league_unit_name",
        "hatstats",
        "attack",
        "midfield",
        "defense",
        "loddar_stats",
        "tsi",
        "salary",
        "rating",
        "rating_end_of_match",
        "age",
        "injury",
        "power_rating",
        "founded"
      )
      .from("hattrick.team_rankings")
      .where
        .leagueId(leagueId)
        .season(currentSeason)
        .round(currentRound)
        .founded.greaterOrEqual(datesRange.min)
        .founded.less(datesRange.max)
      .limitBy(1, "team_id")

    RestClickhouseDAO.executeZIO(builder.sqlWithParameters().build, rowParser)
  }
}
