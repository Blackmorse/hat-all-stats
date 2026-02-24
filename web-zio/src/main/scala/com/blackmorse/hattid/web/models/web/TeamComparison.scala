package com.blackmorse.hattid.web.models.web

import com.blackmorse.hattid.web.models.clickhouse.TeamRankings
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class TeamComparison(team1Rankings: List[TeamRankings],
                          team2Rankings: List[TeamRankings])

object TeamComparison {
  implicit val jsonEncoder: JsonEncoder[TeamComparison] = DeriveJsonEncoder.gen[TeamComparison]
  
  def empty(): TeamComparison = TeamComparison(List(), List())
}
