package com.blackmorse.hattid.web.databases.requests.overview

import anorm.RowParser
import com.blackmorse.hattid.web.databases.requests.ClickhouseOverviewRequest
import com.blackmorse.hattid.web.databases.requests.model.overview.NumberOverviewPlayerStats
import sqlbuilder.{Select, SqlBuilder}

object NumberOverviewRequest extends ClickhouseOverviewRequest[NumberOverviewPlayerStats] {

  override val rowParser: RowParser[NumberOverviewPlayerStats] = NumberOverviewPlayerStats.mapper

  override def builder(season: Int,
                       round: Int,
                       leagueId: Option[Int],
                       divisionLevel: Option[Int]): SqlBuilder = {
    import SqlBuilder.implicits._

    Select(
        "uniq(team_id)" `as` "numberOfTeams",
        "count()" `as` "numberOfPlayers",
        "countIf(injury_level > 0)" `as` "injuried",
        "sum(goals)" `as` "goals",
        "sum(yellow_cards)" `as` "yellow_cards",
        "sum(red_cards)" `as` "red_cards"
      )
      .from("hattrick.player_stats")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch
  }
}
