package handlers

import controllers.{MessageSupport, WebLeagueDetails, routes}
import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent._
import javax.inject.{Inject, Singleton}
import play.core.server.akkahttp.AkkaHeadersWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

@Singleton
class HattidHttpErrorHandler @Inject()() extends HttpErrorHandler  {
  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    process(request)
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    exception.printStackTrace()
    process(request)
  }

  private def process(request: RequestHeader): Future[Result] = {
    val uri = request.headers.asInstanceOf[AkkaHeadersWrapper].request.uri.toString()

    val leagueRegex = "leagueId=[0-9]+".r

    val leagueId = leagueRegex.findFirstIn(uri).map(leagueIdRequestString => {
      val leagueIdString = leagueIdRequestString.split("=")(1)

      val isInteger = Try(leagueIdString.toInt).isSuccess

      if (isInteger) {
        leagueIdString.toInt
      } else {
        1000
      }
    }).getOrElse (1000)
    Future(Redirect(routes.ErrorController.errorForLeague(leagueId)))
  }
}