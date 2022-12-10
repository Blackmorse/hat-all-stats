package tests

import akka.actor.ActorSystem
import chpp.{ChppRequestExecutor, OauthTokens}
import chpp.worlddetails.WorldDetailsRequest
import com.typesafe.config.ConfigFactory
import hattid.telegram.TelegramClient
import hattid.telegram.TelegramClient.TelegramCreds

import java.sql.{Connection, DriverManager}
import scala.concurrent.duration._
import scala.concurrent.Await

object TestsRunner {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem("TestsActorSystem")

    val config = ConfigFactory.load()

    val authToken = config.getString("tokens.authToken")
    val authCustomerKey = config.getString("tokens.authCustomerKey")
    val clientSecret = config.getString("tokens.clientSecret")
    val tokenSecret = config.getString("tokens.tokenSecret")

    val botToken = config.getString("telegram.botToken")
    val chatId = config.getString("telegram.chatId")

    val databaseUrl = config.getString("database.url")
    val databaseUsername = config.getString("database.username")
    val databasePassword = config.getString("database.password")

    implicit val connection: Connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword)

    implicit val oauthTokens: OauthTokens = OauthTokens(authToken, authCustomerKey, clientSecret, tokenSecret)
    implicit val telegramCreds: TelegramCreds = TelegramCreds(chatId = chatId, botToken = botToken)

    val worldDetails = Await.result(ChppRequestExecutor.execute(WorldDetailsRequest()), 1.minute) match {
      case Right(value) => value
      case Left(err) => throw new Exception(s"$err")
    }

    try {
      val allIssues = AllCountriesTest.testNumberOfCountries(worldDetails) ++
        AllCountriesTest.testFirstAndLastLeague(worldDetails) ++
        AllCountriesTest.testDivisionLevels(worldDetails) ++
        AllCountriesTest.testCupSchedule(worldDetails) ++
        ClickhouseTests.testTeamCounts(worldDetails) ++
        ClickhouseTests.testNumberOfTeamRankingsRecords(worldDetails) ++
        ClickhouseTests.testNoHolesInLeagueRounds(worldDetails)
      if (allIssues.isEmpty) {
        Await.result(TelegramClient.sendMessage("Countries tests succesfully passed!"), 1.minute)
      } else {
        allIssues.foreach(issue => Await.result(TelegramClient.sendMessage(issue), 1.minute))
      }
    } catch {
      case e: Throwable => Await.result(TelegramClient.sendMessage("Tests are broken!! \n" + e.getStackTrace.mkString("\n")), 1.minute)
    } finally {
      connection.close()
    }

    actorSystem.terminate()
  }
}
