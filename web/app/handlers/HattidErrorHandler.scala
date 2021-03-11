package handlers

import javax.inject._
import play.api.http._

class HattidErrorHandler @Inject() (
                                     jsonHandler: JsonHttpErrorHandler,
                                   ) extends PreferredMediaTypeHttpErrorHandler(
  "application/json" -> jsonHandler,
)