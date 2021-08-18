package tests

import akka.actor.ActorSystem
import chpp.worlddetails.WorldDetailsRequest
import chpp.worlddetails.models.WorldDetails
import chpp.{ChppRequestExecutor, OauthTokens}
import com.typesafe.config.ConfigFactory
import hattid.telegram.TelegramClient
import hattid.telegram.TelegramClient.TelegramCreds
import hattid.{CommonData, CupSchedule, ScheduleEntry}

import java.util.Calendar
import scala.concurrent.Await
import scala.concurrent.duration._

object AllCountriesTest {

  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem("TestsActorSystem")

    val config = ConfigFactory.load()

    val authToken = config.getString("tokens.authToken")
    val authCustomerKey = config.getString("tokens.authCustomerKey")
    val clientSecret = config.getString("tokens.clientSecret")
    val tokenSecret = config.getString("tokens.tokenSecret")

    val botToken = config.getString("telegram.botToken")
    val chatId = config.getString("telegram.chatId")

    implicit val oauthTokens: OauthTokens = OauthTokens(authToken, authCustomerKey, clientSecret, tokenSecret)
    implicit val telegramCreds: TelegramCreds = TelegramCreds(chatId = chatId, botToken = botToken)

    val worldDetails = Await.result(ChppRequestExecutor.execute(WorldDetailsRequest()), 1.minute)

    try {
      testNumberOfCountries(worldDetails)
      testFirstAndLastLeague(worldDetails)
      testCupSchedule(worldDetails)
      Await.result(TelegramClient.sendMessage("Countries tests succesfully passed!"), 1.minute)
    } catch {
      case e: Exception =>
        Await.result(TelegramClient.sendMessage(e.getMessage), 1.minute)
    }

    actorSystem.terminate()
  }

  private def testNumberOfCountries(worldDetails: WorldDetails): Unit = {
    if (CommonData.higherLeagueMap.size != worldDetails.leagueList.size) {
      throw new Exception(s"Expected ${CommonData.higherLeagueMap.size} countries, but got ${worldDetails.leagueList.size}")
    }
  }

  private def testCupSchedule(worldDetails: WorldDetails): Unit = {

    val schedule = CupSchedule.normalizeCupScheduleToDayOfWeek(CupSchedule.seq, Calendar.MONDAY)
      .sortBy(_.date)

    val worldDetailsSchedule = CupSchedule.normalizeCupScheduleToDayOfWeek(worldDetails.leagueList
      .map(league => ScheduleEntry(league.leagueId, league.cupMatchDate.get)), Calendar.MONDAY)
      .sortBy(_.date)

    schedule.zip(worldDetailsSchedule)
      .foreach{case (original, worldDetails) =>
        if (original.date != worldDetails.date) {
          throw new Exception(s"Cup schedule has been changed. Backup: $original. World details: $worldDetails")
        }
      }
  }

  private def testFirstAndLastLeague(worldDetails: WorldDetails): Unit = {
    val schedule = CupSchedule.normalizeCupScheduleToDayOfWeek(worldDetails.leagueList
      .map(league => ScheduleEntry(league.leagueId, league.seriesMatchDate)), Calendar.THURSDAY)
      .sortBy(_.date)

    if (schedule.head.leagueId != 1000) {
      throw new Exception(s"Now first league is ${schedule.head} instead of Hattrick International!")
    }
    if (schedule.last.leagueId != 100) {
      throw new Exception(s"Now last league is ${schedule.last} instead of El Salvador!")
    }
  }
}
