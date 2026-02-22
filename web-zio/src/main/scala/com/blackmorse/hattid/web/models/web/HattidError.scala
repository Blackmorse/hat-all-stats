package com.blackmorse.hattid.web.models.web

trait HattidError {
}

case class BadGatewayError(description: String) extends HattidError

case class BadRequestError(description: String) extends HattidError

case class NotFoundError(entityType: String,
                         entityId: String,
                         description: String) extends HattidError
  
case class DbError(dbException: Throwable) extends HattidError

case class HattidInternalError(description: String) extends HattidError

case class SqlInjectionError() extends HattidError

object NotFoundError {
  val PLAYER: String = "PLAYER"
  val TEAM: String = "TEAM"
  val LEAGUE: String = "COUNTRY"
  val DIVISION_LEVEL: String = "DIVISION_LEVEL"
  val LEAGUE_UNIT: String = "LEAGUE"
  val MATCH: String = "MATCH"
}
