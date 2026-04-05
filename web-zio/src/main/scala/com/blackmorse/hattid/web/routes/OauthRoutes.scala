package com.blackmorse.hattid.web.routes

import com.blackmorse.hattid.web.databases.ClickhousePool.ClickhousePool
import com.blackmorse.hattid.web.databases.dao.InsertClickhouseDAO
import com.blackmorse.hattid.web.models.web.{BadRequestError, HattidError, OauthError, OauthRequestTokenDoesntExist}
import com.blackmorse.hattid.web.oauth.HattrickOauth
import com.blackmorse.hattid.web.webclients.CustomerConfig
import com.blackmorse.hattid.web.zios.{HattidEnv, stringParamOpt}
import zio.http.*
import zio.*
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.{OAuth1AccessToken, OAuth1RequestToken, OAuthAsyncRequestCallback}
import zio.concurrent.ConcurrentMap;


object OauthService {
  def make = ZLayer {
    for {
      customerConfig <- ZIO.service[CustomerConfig]
      map            <- ConcurrentMap.empty[String, String]
    } yield new OauthService(customerConfig, map)
  }
}

class OauthService(customerConfig: CustomerConfig, tokensMap: ConcurrentMap[String, String]) {
  
  private val service = new ServiceBuilder(customerConfig.customerKey)
    .callback("https://hattid.com/api/oauth/callback")
    .apiSecret(customerConfig.customerSecret)
    .build(HattrickOauth)

  def requestToken(): ZIO[Any, OauthError, OAuth1RequestToken] = {
    for {
      requestToken <- ZIO.fromFutureJava(service.getRequestTokenAsync)
                        .mapError(error => OauthError(error))
      _            <- tokensMap.put(requestToken.getToken, requestToken.getTokenSecret)
      _            <- tokensMap.remove(requestToken.getToken).delay(10.minutes).fork
    } yield requestToken
  }

  def redirectUrl(requestToken: OAuth1RequestToken): String = service.getAuthorizationUrl(requestToken)

  def grantAccessToken(requestToken: String, oauthVerifier: String): IO[OauthError | OauthRequestTokenDoesntExist.type, OAuth1AccessToken] = {
    for {
      requestTokenSecret <- tokensMap.get(requestToken).someOrFail(OauthRequestTokenDoesntExist)
      _                  <- ZIO.debug(s"Granting access token with requestToken: $requestToken, requestTokenSecret: $requestTokenSecret, oauthVerifier: $oauthVerifier")
      accessToken        <- ZIO.fromFutureJava(
                              service.getAccessTokenAsync(new OAuth1RequestToken(requestToken, requestTokenSecret), oauthVerifier)
                            ).mapError(error => OauthError(error))
    } yield accessToken
  }
}

object OauthRoutes {
  val routes: Seq[Route[HattidEnv & OauthService & ClickhousePool, HattidError]] = Seq(
    Method.GET / "api" / "oauth" / "requestToken" -> requestToken,
    Method.GET / "api" / "oauth" / "callback" -> callback
  )

  private def callback = handler { (req: Request) => {
      for {
        oauthService  <- ZIO.service[OauthService]
        _             <- ZIO.debug("Callback called with the argument" + req.url.toString)
        requestToken  <- req.stringParamOpt("oauth_token").someOrFail(BadRequestError("Missing oauth_token parameter"))
        oauthVerifier <- req.stringParamOpt("oauth_verifier")
        _             <- ZIO.debug(s"Callback received with oauth_token: $requestToken and oauth_verifier: $oauthVerifier")
        accessToken   <- oauthService.grantAccessToken(requestToken, oauthVerifier.getOrElse(""))
        _             <- ZIO.debug(s"Access token granted: ${accessToken.getToken}, secret: ${accessToken.getTokenSecret}")
        _             <- InsertClickhouseDAO.insertOauthTokens(requestToken, accessToken.getToken, accessToken.getTokenSecret)
                           .tapError(e => ZIO.succeed(e.printStackTrace()))
                           .mapError(error => OauthError(error))
      } yield Response.text(requestToken)
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
