package com.blackmorse.hattid.web.service.cache

import com.blackmorse.hattid.web.databases.ClickhousePool.ClickhousePool
import com.blackmorse.hattid.web.databases.requests.OrderingKeyPath
import com.blackmorse.hattid.web.databases.requests.model.player.DreamTeamPlayer
import com.blackmorse.hattid.web.databases.requests.playerstats.dreamteam.DreamTeamRequest
import com.blackmorse.hattid.web.models.web.{HattidError, StatsType}
import zio.cache.{Cache, Lookup}
import zio.{Duration, ZLayer}

class DreamTeamCache(val cache: DreamTeamCache.CacheType) 

object DreamTeamCache {
  type DreamTeamCacheKey = (OrderingKeyPath, StatsType, String)
  type CacheType = Cache[DreamTeamCacheKey, HattidError, List[DreamTeamPlayer]]

  def make: ZLayer[ClickhousePool, Nothing, DreamTeamCache] = ZLayer.fromZIO {
    Cache.make(
      capacity = 10000,
      timeToLive = Duration.Infinity,
      lookup = Lookup({ (key: (OrderingKeyPath, StatsType, String)) => DreamTeamRequest.execute(key._1, key._2, key._3) })
    ).map(cache => new DreamTeamCache(cache))
  }
}
