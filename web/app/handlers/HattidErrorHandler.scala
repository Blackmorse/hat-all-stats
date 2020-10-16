package handlers

import javax.inject._
import play.api.http._

class HattidErrorHandler @Inject() (
                                     jsonHandler: JsonHttpErrorHandler,
                                     htmlHandler: HattidHttpErrorHandler,
                                   ) extends PreferredMediaTypeHttpErrorHandler(
  "application/json" -> jsonHandler,
  "text/html"        -> htmlHandler,
)