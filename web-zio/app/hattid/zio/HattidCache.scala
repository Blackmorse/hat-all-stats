package hattid.zio

import cache.ZioCacheModule.ZDreamTeamCache
import databases.requests.OrderingKeyPath
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import models.web.StatsType
import zio.{Duration, URIO}
import zio.cache.{Cache, Lookup}

object HattidCache {
  val zDreamTeamCache: URIO[DBServices, ZDreamTeamCache] = Cache.make(
    capacity = 10000,
    timeToLive = Duration.Infinity,
    lookup = Lookup({ (key: (OrderingKeyPath, StatsType, String)) => DreamTeamRequest.execute(key._1, key._2, key._3) })
  )
}
