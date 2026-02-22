package com.blackmorse.hattid.web.zios

import com.blackmorse.hattid.web.databases.requests.OrderingKeyPath
import com.blackmorse.hattid.web.databases.requests.model.player.DreamTeamPlayer
import com.blackmorse.hattid.web.databases.requests.playerstats.dreamteam.DreamTeamRequest
import com.blackmorse.hattid.web.models.web.{HattidError, StatsType}
import zio.cache.{Cache, Lookup}
import zio.{Duration, URIO}

object HattidCache {
  type DreamTeamCacheKey = (OrderingKeyPath, StatsType, String)
  type ZDreamTeamCache = Cache[DreamTeamCacheKey, HattidError, List[DreamTeamPlayer]]
  val zDreamTeamCache: URIO[DBServices, ZDreamTeamCache] = Cache.make(
    capacity = 10000,
    timeToLive = Duration.Infinity,
    lookup = Lookup({ (key: (OrderingKeyPath, StatsType, String)) => DreamTeamRequest.execute(key._1, key._2, key._3) })
  )
}
