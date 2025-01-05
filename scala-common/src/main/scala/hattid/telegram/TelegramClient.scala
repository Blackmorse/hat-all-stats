package hattid.telegram

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse}

import scala.concurrent.Future


object TelegramClient {
  case class TelegramCreds(chatId: String, botToken: String)

  def sendMessage(message: String)(implicit telegramCreds: TelegramCreds, actorSystem: ActorSystem): Future[HttpResponse] = {
    Http().singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = s"https://api.telegram.org/bot${telegramCreds.botToken}/sendMessage",
      entity = HttpEntity(ContentTypes.`application/json`,
        s"""{\"chat_id\":\"${telegramCreds.chatId}\", \"text\": \"$message\"}"""
      )
    ))
  }
}
