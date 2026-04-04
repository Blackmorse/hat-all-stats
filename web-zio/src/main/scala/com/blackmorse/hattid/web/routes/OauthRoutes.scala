package com.blackmorse.hattid.web.routes

import chpp.ChppRequestData
import com.blackmorse.hattid.web.models.web.{BadRequestError, HattidError, OauthError}
import com.blackmorse.hattid.web.oauth.HattrickOauth
import com.blackmorse.hattid.web.webclients.CustomerConfig
import com.blackmorse.hattid.web.zios.{HattidEnv, stringParamOpt}
import org.apache.commons.codec.binary.Base64
import zio.http.*
import zio.*

import java.net.URLEncoder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import scala.util.Random
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.{OAuth1AccessToken, OAuth1RequestToken, OAuthAsyncRequestCallback};


object OauthService {
  def make = ZLayer {
    for {
      tokenRef <- Ref.make("")
      tokenSecretRef <- Ref.make("")
      customerConfig <- ZIO.service[CustomerConfig]
    } yield new OauthService(customerConfig, tokenRef, tokenSecretRef)
  }
}

class OauthService(customerConfig: CustomerConfig, tokenRef: Ref[String], tokenSecretRef: Ref[String]) {
  
  private val service = new ServiceBuilder(customerConfig.customerKey)
    .callback("https://hattid.com/api/oauth/callback")
    .apiSecret(customerConfig.customerSecret)
    .build(HattrickOauth)

  def requestToken(): ZIO[Any, OauthError, OAuth1RequestToken] = {
    for {
      tokens <- ZIO.fromFutureJava(service.getRequestTokenAsync)
        .mapError(error => OauthError(error))
      _ <- tokenRef.set(tokens.getToken)
      _ <- tokenSecretRef.set(tokens.getTokenSecret)
    } yield tokens
  }

  def redirectUrl(requestToken: OAuth1RequestToken): String = service.getAuthorizationUrl(requestToken)

  def grantAccessToken(oauthVerifier: String): IO[OauthError, OAuth1AccessToken] = { 
    for {
      requestToken <- tokenRef.get
      requestTokenSecret <- tokenSecretRef.get
      _ <- Console.printLine(s"Granting access token with requestToken: $requestToken, requestTokenSecret: $requestTokenSecret, oauthVerifier: $oauthVerifier")
             .orElseFail(OauthError(new Exception("Failed to print access token parameters to console")))
      accessToken <- ZIO.fromFutureJava(
            service.getAccessTokenAsync(new OAuth1RequestToken(requestToken, requestTokenSecret), oauthVerifier)
          ).mapError(error => OauthError(error))
    } yield accessToken
  }
}

object OauthRoutes {
  val routes: Seq[Route[HattidEnv & OauthService, HattidError]] = Seq(
    Method.GET / "api" / "oauth" / "requestToken" -> requestToken,
    Method.GET / "api" / "oauth" / "callback" -> callback
  )

  private def callback = handler { (req: Request) => {
    for {
      oauthService <- ZIO.service[OauthService]
      _ <- Console.printLine("Callback called with the argument" + req.url.toString).orElseFail(BadRequestError("Failed to print to console"))
      oauthToken <- req.stringParamOpt("oauth_token")
      oauthVerifier <- req.stringParamOpt("oauth_verifier")
      _ <- Console.printLine(s"Callback received with oauth_token: $oauthToken and oauth_verifier: $oauthVerifier")
             .orElseFail(BadRequestError("Failed to print callback parameters to console"))
      accessToken <- oauthService.grantAccessToken(oauthVerifier.getOrElse(""))
      _ <- Console.printLine(s"Access token granted: ${accessToken.getToken}, secret: ${accessToken.getTokenSecret}")
             .orElseFail(BadRequestError("Failed to print access token to console"))
    } yield Response.text(s"Callback received with oauth_token: $oauthToken and oauth_verifier: $oauthVerifier")
  }
  }

  private def requestToken = handler { (req: Request) => {

      for {
        oauthService <- ZIO.service[OauthService]
        token <- oauthService.requestToken()
        redirectUrl = oauthService.redirectUrl(token)
        _ <- Console.printLine(s"Redirecting user to: $redirectUrl")
               .orElseFail(OauthError(new Exception("Failed to print redirect URL to console")))
      } yield Response.text(redirectUrl)
    }
  }
}
