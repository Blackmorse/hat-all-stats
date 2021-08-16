package handlers

import javax.inject._
import play.api.http._
import play.api.mvc.{RequestHeader, Result}
import webclients.WebTelegramClient

import scala.concurrent.Future

class HattidErrorHandler @Inject() (telegramClient: WebTelegramClient,
                                     jsonHandler: JsonHttpErrorHandler,
                                   ) extends PreferredMediaTypeHttpErrorHandler(
  "application/json" -> new HttpErrorHandler {
    override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
      telegramClient.sendMessage(s"${request.path}: \nWeb onClientError: $message")
      jsonHandler.onClientError(request, statusCode, message)
    }

    override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
      telegramClient.sendMessage(s"${request.path} \n\nWeb onServerError: ${exception.getMessage}. Stack Trace: \n ${exception.getStackTrace.mkString("\n")}")
      jsonHandler.onServerError(request, exception)
    }
  },
)