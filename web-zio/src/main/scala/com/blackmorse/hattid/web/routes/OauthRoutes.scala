package com.blackmorse.hattid.web.routes

import com.blackmorse.hattid.web.databases.ClickhousePool.ClickhousePool
import com.blackmorse.hattid.web.databases.dao.InsertClickhouseDAO
import com.blackmorse.hattid.web.models.clickhouse.OauthTokenCh
import com.blackmorse.hattid.web.models.web.{BadRequestError, HattidError, OauthError, OauthRequestTokenDoesntExist}
import com.blackmorse.hattid.web.oauth.{OauthCache, OauthService}
import com.blackmorse.hattid.web.service.ChppService
import com.blackmorse.hattid.web.webclients.AccessConfig
import com.blackmorse.hattid.web.zios.{HattidEnv, stringParamOpt}
import zio.*
import zio.http.*
import zio.json.EncoderOps;


object OauthRoutes {
  val routes: Seq[Route[HattidEnv & OauthService & ClickhousePool & OauthCache.CacheType, HattidError]] = Seq(
    Method.GET / "api" / "oauth" / "requestToken" -> requestToken,
    Method.GET / "api" / "oauth" / "callback" -> callback,
    Method.GET / "api" / "oauth" / "userContext" -> userContext
  )
  
  private def userContext = handler { (req: Request) =>
    for {
      requestToken <- req.stringParamOpt("request_token").someOrFail(OauthRequestTokenDoesntExist)
      oauthCache   <- ZIO.service[OauthCache.CacheType]
      userContext  <- oauthCache.get(requestToken)
    } yield Response.json(userContext.toJson)
  }

  private def callback = handler { (req: Request) => {
      for {
        oauthService  <- ZIO.service[OauthService]
        chppService   <- ZIO.service[ChppService]
        oauthCache    <- ZIO.service[OauthCache.CacheType]
        requestToken  <- req.stringParamOpt("oauth_token").someOrFail(BadRequestError("Missing oauth_token parameter"))
        oauthVerifier <- req.stringParamOpt("oauth_verifier")
        accessToken   <- oauthService.grantAccessToken(requestToken, oauthVerifier.getOrElse(""))
        manager       <- chppService.managerCompendium(AccessConfig(accessToken.getToken, accessToken.getTokenSecret))
        time          <- Clock.currentDateTime
        _             <- InsertClickhouseDAO.insertOauthTokens(
                              OauthTokenCh(requestToken = requestToken,
                                accessToken = accessToken.getToken,
                                accessTokenSecret = accessToken.getTokenSecret,
                                creationTime = time.toLocalDateTime,
                                userId = manager.manager.userId)
                            )
//                           .tapError(e => ZIO.succeed(e.printStackTrace()))
                           .mapError(error => OauthError(error))
        userContext   <- oauthCache.get(requestToken)
      } yield Response.json(userContext.toJson)
    }
  }

  private def requestToken = handler { (req: Request) => {
      for {
        oauthService <- ZIO.service[OauthService]
        token        <- oauthService.requestToken()
        redirectUrl  = oauthService.redirectUrl(token)
        _            <- ZIO.debug(s"Redirecting user to: $redirectUrl")
      } yield Response.text(redirectUrl)
    }
  }
}
