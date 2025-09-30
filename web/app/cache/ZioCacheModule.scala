package cache

import cache.ZioCacheModule.ZDreamTeamCache
import com.google.inject.{AbstractModule, TypeLiteral}
import com.google.inject.name.Names
import databases.dao.RestClickhouseDAO
import databases.requests.OrderingKeyPath
import databases.requests.model.player.DreamTeamPlayer
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import models.web.{HattidError, StatsType}
import zio.cache.{Cache, Lookup}
import zio.{Duration, URIO}

object ZioCacheModule {
  type DreamTeamCacheKey = (OrderingKeyPath, StatsType, String)
  type ZDreamTeamCache = Cache[DreamTeamCacheKey, HattidError, List[DreamTeamPlayer]]
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
}
