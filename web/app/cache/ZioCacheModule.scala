package cache

import cache.ZioCacheModule.{HattidEnv, ZDreamTeamCache}
import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Provides, TypeLiteral}
import databases.dao.RestClickhouseDAO
import databases.requests.OrderingKeyPath
import databases.requests.model.player.DreamTeamPlayer
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import jakarta.inject.Singleton
import models.web.{HattidError, StatsType}
import service.{ChppService, TranslationsService}
import service.leagueinfo.LeagueInfoServiceZIO
import zio.cache.{Cache, Lookup}
import zio.{cache, *}

object ZioCacheModule {
  type DreamTeamCacheKey = (OrderingKeyPath, StatsType, String)
  type ZDreamTeamCache = Cache[DreamTeamCacheKey, HattidError, List[DreamTeamPlayer]]

  type HattidEnv = LeagueInfoServiceZIO & RestClickhouseDAO & ChppService & TranslationsService
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
  def zioEnvironment(restClickhouseDAO: RestClickhouseDAO, chppService: ChppService): ZEnvironment[HattidEnv] = {
    val clickhouseLayer: ULayer[RestClickhouseDAO] = ZLayer.succeed(restClickhouseDAO)
    val chppServiceLayer: ULayer[ChppService] = ZLayer.succeed(chppService)
    val leagueInfoLayer: ZLayer[RestClickhouseDAO & ChppService, Nothing, LeagueInfoServiceZIO] = LeagueInfoServiceZIO.layer
      .mapError(he => new Exception(he.toString))
      .orDie
    val translationLayer: ZLayer[ChppService, HattidError, TranslationsService] = TranslationsService.layer
    
    val env: ZEnvironment[HattidEnv] = Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.run {
        ZIO.scoped {
          for {
            leagueInfoEnv <- ((clickhouseLayer ++ chppServiceLayer) >>> leagueInfoLayer ).build
            translationEnv <- (chppServiceLayer >>> translationLayer).build
          } yield leagueInfoEnv ++
            translationEnv ++
            ZEnvironment(restClickhouseDAO) ++
            ZEnvironment(chppService)
        }
      }.getOrThrowFiberFailure()
    }

    env
  }
}
