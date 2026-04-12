package com.blackmorse.hattid.web.oauth

import com.blackmorse.hattid.web.models.web.{OauthError, OauthRequestTokenDoesntExist}
import com.blackmorse.hattid.web.webclients.CustomerConfig
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.{OAuth1AccessToken, OAuth1RequestToken}
import zio.{IO, ZIO, ZLayer, durationInt}
import zio.concurrent.ConcurrentMap


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
    .callback("https://hattid.com/login")
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
      accessToken        <- ZIO.fromFutureJava(
        service.getAccessTokenAsync(new OAuth1RequestToken(requestToken, requestTokenSecret), oauthVerifier)
      ).mapError(error => OauthError(error))
      _                  <- tokensMap.remove(requestToken)
    } yield accessToken
  }
}