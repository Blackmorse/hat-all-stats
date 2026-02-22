package com.blackmorse.hattid.web.zios

import com.blackmorse.hattid.web.models.web.*
import zio.http.Response

extension (error: HattidError) {
  def toResponse: Response = error match {
    case BadGatewayError(description) => Response.badGateway(description)
    case BadRequestError(description) => Response.badRequest(description)
    case NotFoundError(entityType, entityId, description) =>
      //TODO
      val json =
        s"""
           |{
           |  "entityType": "$entityType",
           |  "entityId": "$entityId",
           |  "description": "$description"
           |}
           |""".stripMargin
      Response.notFound(json)
    case DbError(dbException) => Response.internalServerError("Internal error")
    case HattidInternalError(description) => Response.internalServerError(description)
    case SqlInjectionError() => Response.badRequest("Illegal parameters")
  }
}
