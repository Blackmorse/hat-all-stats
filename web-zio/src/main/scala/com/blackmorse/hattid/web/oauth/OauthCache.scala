package com.blackmorse.hattid.web.oauth

import com.blackmorse.hattid.web.databases.ClickhousePool.ClickhousePool
import com.blackmorse.hattid.web.databases.requests.oauthtokens.OauthTokensRequest
import com.blackmorse.hattid.web.models.web.{HattidError, OauthRequestTokenDoesntExist, UserContext}
import com.blackmorse.hattid.web.service.ChppService
import com.blackmorse.hattid.web.webclients.AccessConfig
import zio.*
import zio.cache.{Cache, Lookup}

object OauthCache {
  type CacheType = Cache[String, HattidError, UserContext]
  
  val make: ZLayer[ClickhousePool & ChppService, Nothing, CacheType] = ZLayer.fromZIO {
    Cache.make(
      capacity = 1000,
      timeToLive = 1.hour,
      lookup = Lookup({ (key: String) => 
        for {
          chppService  <- ZIO.service[ChppService]
          oauthTokenCh <- OauthTokensRequest.execute(key).someOrFail(OauthRequestTokenDoesntExist)
          manager      <- chppService.managerCompendium(AccessConfig(oauthTokenCh.accessToken, oauthTokenCh.accessTokenSecret))
        } yield UserContext(
          userId = manager.manager.userId,
          name = manager.manager.loginName,
          teams  = manager.manager.teams.map(
            team => UserContext.Team(team.teamId, team.teamName)
          )
        )
      })
    )
  }
}
