package cache

import cache.ZioCacheModule.{HattidEnv, ZDreamTeamCache}
import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Provides, TypeLiteral}
import databases.ClickhousePool.ClickhousePool
import databases.dao.RestClickhouseDAO
import databases.requests.OrderingKeyPath
import databases.requests.model.player.DreamTeamPlayer
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import jakarta.inject.Singleton
import models.web.{HattidError, StatsType}
import play.api.Configuration
import service.{ChppService, TranslationsService}
import service.leagueinfo.LeagueInfoServiceZIO
import webclients.AuthConfig
import zio.cache.{Cache, Lookup}
import zio.{cache, *}
import zio.config.*
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider
//import zio.http.ZClient.Config
import zio.http.{Client, ClientDriver, DnsResolver}
import zio.http.netty.NettyConfig
import zio.http.netty.client.NettyClientDriver

import java.net.http.HttpClient
import java.sql.{Connection, DriverManager}
import java.util.Properties


object ZioCacheModule {
  implicit val authConfig: Config[AuthConfig] = deriveConfig[AuthConfig].nested("hattrick")

  type DreamTeamCacheKey = (OrderingKeyPath, StatsType, String)
  type ZDreamTeamCache = Cache[DreamTeamCacheKey, HattidError, List[DreamTeamPlayer]]

  type HattidEnv = LeagueInfoServiceZIO &
    RestClickhouseDAO &
    ChppService &
    TranslationsService &
    DatabaseConfig &
    AuthConfig &
    ClickhousePool &
    Client
}

case class DatabaseConfig(driver: String,
                          url: String,
                          logStatements: Boolean,
                          user: Option[String],
                          password: Option[String])

object DatabaseConfig {
  implicit val config: Config[DatabaseConfig] = deriveConfig[DatabaseConfig].nested("db", "default")
}

class ZioCacheModule extends AbstractModule {
  override def configure(): Unit = {

    val zDreamTeamCache: URIO[ClickhousePool & RestClickhouseDAO, ZDreamTeamCache] = Cache.make(
      capacity = 10000,
      timeToLive = Duration.Infinity,
      lookup = Lookup({ (key: (OrderingKeyPath, StatsType, String)) => DreamTeamRequest.execute(key._1, key._2, key._3) })
    )

    bind(new TypeLiteral[URIO[ClickhousePool & RestClickhouseDAO, ZDreamTeamCache]](){})
      .annotatedWith(Names.named("DreamTeamCache"))
      .toInstance(zDreamTeamCache)
  }

  @Provides
  @Singleton
  def zioEnvironment(restClickhouseDAO: RestClickhouseDAO,
                     chppService: ChppService,
                     configuration: Configuration): ZEnvironment[HattidEnv] = {
    val clickhouseLayer: ULayer[RestClickhouseDAO] = ZLayer.succeed(restClickhouseDAO)
    val chppServiceLayer: ULayer[ChppService] = ZLayer.succeed(chppService)
    val leagueInfoLayer: ZLayer[Client & AuthConfig & ClickhousePool & RestClickhouseDAO & ChppService, Nothing, LeagueInfoServiceZIO] = LeagueInfoServiceZIO.layer
      .mapError(he => new Exception(he.toString))
      .orDie
    val translationLayer: ZLayer[Client & AuthConfig & ChppService, HattidError, TranslationsService] = TranslationsService.layer

    val databaseConfigLayer: ZLayer[Any, Config.Error, DatabaseConfig] = ZLayer {
      TypesafeConfigProvider
        .fromTypesafeConfig(configuration.underlying)
        .load(DatabaseConfig.config)
    }


    val chppAuthConfigLayer: ZLayer[Any, Config.Error, AuthConfig] = ZLayer {
      TypesafeConfigProvider
        .fromTypesafeConfig(configuration.underlying)
        .load(ZioCacheModule.authConfig)
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
    } yield  DriverManager.getConnection(url, properties)


    val acquireRelease = ZIO.acquireRelease(acquire)(conn => ZIO.succeed(conn.close()))

    val zPool = ZPool.make(
      get = acquireRelease,
      range = 10 to 20,
      timeToLive = Duration.fromMillis(30000L)
    )

    val poolLayer: ZLayer[DatabaseConfig & Scope, Nothing, ZPool[Nothing, Connection]] = ZLayer.fromZIO(zPool)
    val httpClientLayer: ZLayer[Any, Throwable, Client] = Client.default


    val env: ZEnvironment[HattidEnv] = Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.run {
        ZIO.scoped {
          for {
            leagueInfoEnv  <- ((clickhouseLayer ++ chppServiceLayer ++ chppAuthConfigLayer ++ httpClientLayer ++ (databaseConfigLayer >>> poolLayer)) >>> leagueInfoLayer).build
            translationEnv <- ((chppAuthConfigLayer ++ chppServiceLayer ++ httpClientLayer) >>> translationLayer).build
            dbConfigEnv    <- databaseConfigLayer.build
            chppConfigEnv  <- chppAuthConfigLayer.build
            zPoolEnv       <- (databaseConfigLayer >>> poolLayer).build
            httpClientEnv  <- httpClientLayer.build
//            netty          <- (ZLayer.succeed(NettyConfig.default) >>> NettyClientDriver.live).build
          } yield leagueInfoEnv ++
            translationEnv ++
            ZEnvironment(restClickhouseDAO) ++
            ZEnvironment(chppService) ++
            dbConfigEnv ++
            chppConfigEnv ++
            zPoolEnv ++
            httpClientEnv
//            netty
        }
      }.getOrThrowFiberFailure()
    }

    env
  }
}
