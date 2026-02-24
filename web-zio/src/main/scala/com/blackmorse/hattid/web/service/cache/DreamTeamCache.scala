package com.blackmorse.hattid.web.service.cache

import com.blackmorse.hattid.web.databases.ClickhousePool.ClickhousePool
import com.blackmorse.hattid.web.databases.requests.OrderingKeyPath
import com.blackmorse.hattid.web.databases.requests.model.player.DreamTeamPlayer
import com.blackmorse.hattid.web.databases.requests.playerstats.dreamteam.DreamTeamRequest
import com.blackmorse.hattid.web.models.web.{HattidError, StatsType}
import zio.{Duration, ZLayer}
import zio.cache.{Cache, Lookup}

object DreamTeamCache {
  type DreamTeamCacheKey = (OrderingKeyPath, StatsType, String)
  type CacheType = Cache[DreamTeamCacheKey, HattidError, List[DreamTeamPlayer]]
  val layer: ZLayer[ClickhousePool, Nothing, CacheType] = ZLayer.fromZIO {
    Cache.make(
      capacity = 10000,
      timeToLive = Duration.Infinity,
      lookup = Lookup({ (key: (OrderingKeyPath, StatsType, String)) => DreamTeamRequest.execute(key._1, key._2, key._3) })
    )
  }
}
