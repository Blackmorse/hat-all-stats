package webclients

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import hattid.telegram.TelegramClient
import hattid.telegram.TelegramClient.TelegramCreds
import play.api.Configuration

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class WebTelegramClient @Inject()(val configuration: Configuration,
                                  implicit val actorSystem: ActorSystem) {
  private implicit val telegramCreds: TelegramCreds = {
    TelegramCreds(chatId = configuration.get[String]("telegram.chatId"),
      botToken = configuration.get[String]("telegram.botToken"))
  }

  def sendMessage(message: String): Future[HttpResponse] =
    TelegramClient.sendMessage(message)
}
