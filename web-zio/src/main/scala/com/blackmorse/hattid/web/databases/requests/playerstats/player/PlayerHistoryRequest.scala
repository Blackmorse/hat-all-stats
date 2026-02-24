package com.blackmorse.hattid.web.databases.requests.playerstats.player

import anorm.RowParser
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.DBIO
import com.blackmorse.hattid.web.databases.requests.model.player.PlayerHistory
import sqlbuilder.{Select, SqlBuilder}
import zio.ZIO

object PlayerHistoryRequest extends ClickhouseRequest[PlayerHistory] {
  override val rowParser: RowParser[PlayerHistory] = PlayerHistory.mapper
  import sqlbuilder.SqlBuilder.implicits.*

  def builder(playerId: Long): SqlBuilder =
    Select(
      "league_id",
      "league_unit_id",
      "league_unit_name",
      "player_id",
      "first_name",
      "last_name",
      "team_id",
      "team_name",
      "(age * 112) + days" `as` "age",
      "tsi",
      "rating",
      "rating_end_of_match",
      "cup_level",
      ClickhouseRequest.roleIdCase("role_id") `as` "role",
      "played_minutes",
      "injury_level",
      "salary",
      "yellow_cards",
      "red_cards",
      "goals",
      "season",
      "round",
      "nationality"
    ).from("hattrick.player_stats")
      .where
      //TODO: ability to add typed parameters with custom name
      .and(s"player_id = $playerId")
      .orderBy("season", "round", "cup_level".asc)
      .setting("optimize_read_in_order", 0)

  def execute(playerId: Long): DBIO[List[PlayerHistory]] = wrapErrors {
    RestClickhouseDAO.executeZIO(builder(playerId).sqlWithParameters().build, rowParser)
  }
}
