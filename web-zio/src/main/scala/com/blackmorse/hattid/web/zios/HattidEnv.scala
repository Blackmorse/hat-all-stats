package com.blackmorse.hattid.web.zios

import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.models.web.HattidError
import com.blackmorse.hattid.web.service.leagueinfo.LeagueInfoServiceZIO
import com.blackmorse.hattid.web.service.leagueunit.LeagueUnitCalculatorService
import com.blackmorse.hattid.web.service.*
import com.blackmorse.hattid.web.service.cache.OverviewCache
import com.blackmorse.hattid.web.webclients.{AuthConfig, ChppClient}
//import com.clickhouse.jdbc.ClickHouseDriver
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.{Config, Duration, Scope, ULayer, ZEnvironment, ZIO, ZLayer, ZPool}

import java.sql.{Connection, DriverManager}
import java.util.Properties

case class DatabaseConfig(driver: String,
                          url: String,
                          logStatements: Boolean,
                          user: Option[String],
                          password: Option[String])

object DatabaseConfig {
  implicit val config: Config[DatabaseConfig] = deriveConfig[DatabaseConfig].nested("db", "default")
}

object HattidEnv {
  implicit val authConfig: Config[AuthConfig] = deriveConfig[AuthConfig].nested("hattrick")

  def env(configPath: String): ZIO[Scope, Throwable, ZEnvironment[HattidEnv]] = {
    val chppAuthConfigLayer = ZLayer {
      TypesafeConfigProvider
        .fromHoconFilePath(configPath)
        .load(authConfig)
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
    } yield {
//      Class.forName("")
//      com.clickhouse.jdbc.ClickHouseDriver.
      new ru.yandex.clickhouse.ClickHouseDriver().connect(url, properties)
//      DriverManager.getConnection(url, properties)
    }


    val acquireRelease = ZIO.acquireRelease(acquire)(conn => ZIO.succeed(conn.close()))

    val zPool = ZPool.make(
      get = acquireRelease,
      range = 10 to 20,
      timeToLive = Duration.fromMillis(30000L)
    )

    val clickhouseLayer: ULayer[RestClickhouseDAO] = ZLayer.succeed(new RestClickhouseDAO())

    val leagueInfoLayer = LeagueInfoServiceZIO.layer

    val chppServiceLayer: ULayer[ChppService] = ZLayer.succeed(new ChppService)
    val httpClientLayer: ZLayer[Any, Throwable, Client] = Client.default
    val chppClientLayer: ULayer[ChppClient] = ZLayer.succeed(new ChppClient)
    val poolLayer: ZLayer[DatabaseConfig & Scope, Nothing, ZPool[Nothing, Connection]] = ZLayer.fromZIO(zPool)
    val translationLayer = TranslationsService.layer
      .mapError(he => new Exception(he.toString))
//      .orDie

    val res: ZIO[Scope, Throwable | HattidError, ZEnvironment[HattidEnv]] = for {
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
      similarMatchesServiceEnv <- ZLayer.succeed(new SimilarMatchesService()).build
      overviewCache <- ((clickhouseLayer ++ (databaseConfigLayer >>> poolLayer)) >>> OverviewCache.layer).build
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
        similarMatchesServiceEnv ++
        overviewCache
    }

    res
      .tapError(e => ZIO.logError(e.toString))
      .mapError{
        case e: HattidError => new Exception(e.toString)
        case e: Throwable => e
      }
  }
}
