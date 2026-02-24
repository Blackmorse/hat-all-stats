package chpp

import chpp.chpperror.ChppError
import com.lucidchart.open.xtract.XmlReader
import org.apache.pekko.actor.{ActorSystem, Scheduler}
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.headers.RawHeader
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest}
import org.apache.pekko.http.scaladsl.settings.ConnectionPoolSettings
import org.apache.pekko.util.ByteString

import scala.concurrent.Future
import scala.concurrent.duration.*
import scala.util.{Failure, Success, Try}


object ChppRequestExecutor {
  private val retries = 4

  def executeWithRetry[Model](request: AbstractRequest[Model])
                      (implicit oauthTokens: OauthTokens, system: ActorSystem, reader: XmlReader[Model]): Future[Either[ChppError, Model]] = {
    import system.dispatcher
    implicit val scheduler: Scheduler =  system.scheduler
    //Throwing exceptions insides to enable retries, as it triggered by throwing exceptions
    org.apache.pekko.pattern.retry(
      attempt = () => execute(request),
      attempts = retries,
      minBackoff = 800.millisecond,
      maxBackoff = 10.seconds,
      randomFactor = 0.3
    ) transform {
      case Success(model) => Try(Right(model))
      case Failure(ChppErrorResponse(chppError)) => Try(Left(chppError))
      case Failure(e) => Try(throw e)
    }
  }

 
  
  def execute[Model](request: AbstractRequest[Model])
                       (implicit oauthTokens: OauthTokens, system: ActorSystem, reader: XmlReader[Model]): Future[Model] = {
    import system.dispatcher

    val poolSettings = ConnectionPoolSettings(system)
      .withMaxConnections(100) // Maximum connections to the same host
      .withPipeliningLimit(10) // Maximum pipelined requests per connection
    val requestData = request.requestData(oauthTokens)
    
    val singleRequest = HttpRequest(uri = requestData.uri,
      entity = HttpEntity.Strict(ContentTypes.`text/xml(UTF-8)`, data = ByteString.empty))
      .withHeaders(
        RawHeader("Authorization", requestData.header),
      )
    
    for (response <- Http().singleRequest(singleRequest);
                 responseBody <- response.entity.toStrict(3.minute)) yield {
      ResponseParser.parseResponse(request, responseBody.data.utf8String)
    }
  }
}
