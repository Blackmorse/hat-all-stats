package models.web

import play.api.libs.json.{Json, OWrites}
import play.api.mvc.Result
import play.api.mvc.Results.{BadGateway, BadRequest, InternalServerError, NotFound}

trait HattidError {
  def toPlayHttpResult: Result
}

case class BadGatewayError(description: String) extends HattidError:
  override def toPlayHttpResult: Result = BadGateway(description)

case class BadRequestError(description: String) extends HattidError:
  override def toPlayHttpResult: Result = BadRequest(description)

case class NotFoundError(entityType: String,
                         entityId: String,
                         description: String) extends HattidError:
  override def toPlayHttpResult: Result = NotFound( entityType + " with id " + entityId + " not found. " + description)
  
case class DbError(dbException: Throwable) extends HattidError {
  override def toPlayHttpResult: Result = InternalServerError("Internal error")
}

case class InternalError(description: String) extends HattidError:
  override def toPlayHttpResult: Result = InternalServerError(description)

case class SqlInjectionError() extends HattidError {
  override def toPlayHttpResult: Result = BadRequest("Illegal parameters")
}

object NotFoundError {
  @deprecated
  implicit val writes: OWrites[NotFoundError] = Json.writes[NotFoundError]

  val PLAYER: String = "PLAYER"
  val TEAM: String = "TEAM"
  val LEAGUE: String = "COUNTRY"
  val DIVISION_LEVEL: String = "DIVISION_LEVEL"
  val LEAGUE_UNIT: String = "LEAGUE"
  val MATCH: String = "MATCH"
}
