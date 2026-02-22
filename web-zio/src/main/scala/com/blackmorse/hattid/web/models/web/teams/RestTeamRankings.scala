package com.blackmorse.hattid.web.models.web.teams

import com.blackmorse.hattid.web.models.clickhouse.TeamRankings
import zio.json.{DeriveJsonEncoder, JsonEncoder}

object RestTeamRankings {
  implicit val jsonEncoder: JsonEncoder[RestTeamRankings] = DeriveJsonEncoder.gen[RestTeamRankings]
}

case class RestTeamRankings(teamRankings: Seq[TeamRankings],
                            leagueTeamsCounts: Seq[(Int, Long)],
                            divisionLevelTeamsCounts: Seq[(Int, Long)],
                            currencyRate: Double,
                            currencyName: String)
