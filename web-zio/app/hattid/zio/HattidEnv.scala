package hattid.zio

import cache.{DatabaseConfig, ZioCacheModule}
import databases.dao.RestClickhouseDAO
import service.{ChppService, RestOverviewStatsService, SimilarMatchesService, TeamsService, TranslationsService}
import service.leagueinfo.LeagueInfoServiceZIO
import service.leagueunit.LeagueUnitCalculatorService
import webclients.{AuthConfig, ChppClient}
import zio.{Config, Duration, Scope, ULayer, ZEnvironment, ZIO, ZLayer, ZPool}
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*

import java.sql.{Connection, DriverManager}
import java.util.Properties

object HattidEnv {
  def env(configPath: String): ZIO[Scope, Throwable, ZEnvironment[HattidEnv]] = {
    val chppAuthConfigLayer = ZLayer {
      TypesafeConfigProvider
        .fromHoconFilePath(configPath)
        .load(ZioCacheModule.authConfig)
    }

    val databaseConfigLayer: ZLayer[Any, Config.Error, DatabaseConfig] = ZLayer {
      TypesafeConfigProvider
        .fromHoconFilePath(configPath)
        .load(DatabaseConfig.config)
    }

    val acquire: ZIO[DatabaseConfig, Nothing, Connection] = for {
      dbConfig <- ZIO.service[DatabaseConfig]
      url = s"${dbConfig.url}"
      properties = {
        val p = new Properties()
        p.setProperty("user", dbConfig.user.getOrElse("default"))
        p.setProperty("password", dbConfig.password.getOrElse(""))
        p
      }
    } yield DriverManager.getConnection(url, properties)


    val acquireRelease = ZIO.acquireRelease(acquire)(conn => ZIO.succeed(conn.close()))

    val zPool = ZPool.make(
      get = acquireRelease,
      range = 10 to 20,
      timeToLive = Duration.fromMillis(30000L)
    )

    val clickhouseLayer: ULayer[RestClickhouseDAO] = ZLayer.succeed(new RestClickhouseDAO())

    val leagueInfoLayer: ZLayer[CHPPServices & DBServices , Nothing, LeagueInfoServiceZIO] = LeagueInfoServiceZIO.layer
      .mapError(he => new Exception(he.toString))
      .orDie

    val chppServiceLayer: ULayer[ChppService] = ZLayer.succeed(new ChppService)
    val httpClientLayer: ZLayer[Any, Throwable, Client] = Client.default
    val chppClientLayer: ULayer[ChppClient] = ZLayer.succeed(new ChppClient)
    val poolLayer: ZLayer[DatabaseConfig & Scope, Nothing, ZPool[Nothing, Connection]] = ZLayer.fromZIO(zPool)
    val translationLayer: ZLayer[CHPPServices, Nothing, TranslationsService] = TranslationsService.layer
      .mapError(he => new Exception(he.toString))
      .orDie

    val res: ZIO[Scope, Throwable, ZEnvironment[HattidEnv]] = for {
      chppClientEnv <- ZLayer.succeed(new ChppClient).build
      chppServiceEnv <- chppServiceLayer.build
      authConfigEnv <- chppAuthConfigLayer.build
      databaseConfigEnv <- databaseConfigLayer.build
      serverEnv <- Server.defaultWithPort(9000).build
      httpClientEnv <- Client.default.build
      clickhouseEnv <- clickhouseLayer.build
      calculatorServiceEnv <- ZLayer.succeed(new LeagueUnitCalculatorService()).build
      teamServiceEnv <- ZLayer.succeed(new TeamsService()).build

      zPoolEnv <- (databaseConfigLayer >>> poolLayer).build
      httpClientEnv <- httpClientLayer.build
      leagueInfoEnv <- ((clickhouseLayer ++ chppServiceLayer ++ chppAuthConfigLayer ++ httpClientLayer ++ chppClientLayer ++ (databaseConfigLayer >>> poolLayer)) >>> leagueInfoLayer).build
      translationEnv <- ((chppAuthConfigLayer ++ chppServiceLayer ++ httpClientLayer ++ chppClientLayer) >>> translationLayer).build
      overviewEnv <- ZLayer.succeed(new RestOverviewStatsService()).build
      similarMatchesServiceEnv <- ZLayer.succeed(new SimilarMatchesService()).build
    } yield {
      serverEnv ++
        chppClientEnv ++
        chppServiceEnv ++
        authConfigEnv ++
        databaseConfigEnv ++
        httpClientEnv ++
        clickhouseEnv ++
        zPoolEnv ++
        leagueInfoEnv ++
        calculatorServiceEnv ++
        teamServiceEnv ++
        translationEnv ++
        overviewEnv ++
        similarMatchesServiceEnv
    }

    res
  }
}
