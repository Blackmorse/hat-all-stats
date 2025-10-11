package cache

import cache.ZioCacheModule.{HattidEnv, ZDreamTeamCache}
import chpp.AuthConfig
import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Provides, TypeLiteral}
import databases.dao.RestClickhouseDAO
import databases.requests.OrderingKeyPath
import databases.requests.model.player.DreamTeamPlayer
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import jakarta.inject.Singleton
import models.web.{HattidError, StatsType}
import play.api.Configuration
import service.{ChppService, TranslationsService}
import service.leagueinfo.LeagueInfoServiceZIO
import zio.cache.{Cache, Lookup}
import zio.{cache, *}
import zio.config.*
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider



object ZioCacheModule {
  implicit val authConfig: Config[AuthConfig] = deriveConfig[AuthConfig].nested("hattrick")
  
  type DreamTeamCacheKey = (OrderingKeyPath, StatsType, String)
  type ZDreamTeamCache = Cache[DreamTeamCacheKey, HattidError, List[DreamTeamPlayer]]

  type HattidEnv = LeagueInfoServiceZIO & 
    RestClickhouseDAO &
    ChppService &
    TranslationsService & 
    DatabaseConfig & 
    AuthConfig
}



case class DatabaseConfig(driver: String, url: String, logStatements: Boolean)

object DatabaseConfig {
  implicit val config: Config[DatabaseConfig] = deriveConfig[DatabaseConfig].nested("db", "default")
}

class ZioCacheModule extends AbstractModule {
  override def configure(): Unit = {

    val zDreamTeamCache: URIO[RestClickhouseDAO, ZDreamTeamCache] = Cache.make(
      capacity = 10000,
      timeToLive = Duration.Infinity,
      lookup = Lookup({ (key: (OrderingKeyPath, StatsType, String)) => DreamTeamRequest.execute(key._1, key._2, key._3) })
    )

    bind(new TypeLiteral[URIO[RestClickhouseDAO, ZDreamTeamCache]](){})
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
    val leagueInfoLayer: ZLayer[AuthConfig & RestClickhouseDAO & ChppService, Nothing, LeagueInfoServiceZIO] = LeagueInfoServiceZIO.layer
      .mapError(he => new Exception(he.toString))
      .orDie
    val translationLayer: ZLayer[AuthConfig & ChppService, HattidError, TranslationsService] = TranslationsService.layer
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

    val env: ZEnvironment[HattidEnv] = Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.run {
        ZIO.scoped {
          for {
            leagueInfoEnv  <- ((clickhouseLayer ++ chppServiceLayer ++ chppAuthConfigLayer) >>> leagueInfoLayer ).build
            translationEnv <- ((chppAuthConfigLayer ++ chppServiceLayer) >>> translationLayer).build
            dbConfigEnv    <- databaseConfigLayer.build
            chppConfigEnv  <- chppAuthConfigLayer.build
          } yield leagueInfoEnv ++
            translationEnv ++
            ZEnvironment(restClickhouseDAO) ++
            ZEnvironment(chppService) ++
            dbConfigEnv ++
            chppConfigEnv
        }
      }.getOrThrowFiberFailure()
    }

    env
  }
}
