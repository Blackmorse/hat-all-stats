package telegram

import akka.actor.ActorSystem
import com.typesafe.config.Config
import com.typesafe.scalalogging.Logger
import hattid.telegram.TelegramClient
import hattid.telegram.TelegramClient.TelegramCreds
import org.slf4j.LoggerFactory

import javax.inject.{Inject, Singleton}

@Singleton
class LoaderTelegramClient @Inject()(config: Config,
                                     implicit val actorSystem: ActorSystem) {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
  def sendException(message: String, e: Throwable): Unit = {
    val enabled = config.hasPath("telegram.chatId") && config.hasPath("telegram.botToken")
    if (enabled) {
      implicit val creds: TelegramCreds = TelegramCreds(config.getString("telegram.chatId"), config.getString("telegram.botToken"))
      val stacktrace = e.getStackTrace.mkString("\n")
      val text = s"$message: ${e.getMessage} \n\n $stacktrace"
      try {
        TelegramClient.sendMessage(text.take(4095))
      } catch {
        case e: Exception => logger.warn("Telegram reporting is not working", e)
      }
    }
  }
}
