package com.blackmorse.hattid.web.models.web

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class UserContext(userId: Long, name: String, teams: Seq[UserContext.Team])

object UserContext {
  implicit val jsonEncoder: JsonEncoder[UserContext] = DeriveJsonEncoder.gen[UserContext]

  case class Team(teamId: Long, teamName: String)

  object Team {
    implicit val jsonEncoder: JsonEncoder[Team] = DeriveJsonEncoder.gen[Team]
  }
}


