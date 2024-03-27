package webclients

import org.apache.pekko.actor.ActorSystem
import hattid.telegram.TelegramClient
import hattid.telegram.TelegramClient.TelegramCreds
import play.api.Configuration

import javax.inject.{Inject, Singleton}

@Singleton
class WebTelegramClient @Inject()(val configuration: Configuration,
                                  implicit val actorSystem: ActorSystem) {
  private lazy val telegramCredsOpt: Option[TelegramCreds] =
    for (chatId <- configuration.getOptional[String]("telegram.chatId");
         botToken <- configuration.getOptional[String]("telegram.botToken")
         ) yield {
      TelegramCreds(chatId = chatId,
        botToken = botToken)
    }

  def sendMessage(message: String): Unit =
    telegramCredsOpt.foreach(implicit telegramCreds => TelegramClient.sendMessage(message.take(4095))) // Maximum length of telegram message
}
