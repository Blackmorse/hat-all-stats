package com.blackmorse.hattid.web.models.web.teams

import zio.json.*

case class TeamSearchResult(teamId: Long, teamName: String)

object TeamSearchResult {
  implicit val encoder: JsonEncoder[TeamSearchResult] = DeriveJsonEncoder.gen[TeamSearchResult]
}
