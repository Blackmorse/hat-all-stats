package flows

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL}
import chpp.{AbstractRequest, OauthTokens, RequestCreator}
import com.lucidchart.open.xtract.XmlReader

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import scala.xml.XML

abstract class AbstractHttpFlow[Request <: AbstractRequest, Model] {
  def preprocessBody(body: String): String

  def apply[T]()(implicit oauthTokens: OauthTokens, system: ActorSystem,
               executionContext: ExecutionContext, reader: XmlReader[Model]): Flow[(Request, T), (Model, T), NotUsed] = {
    val flow = Flow[(Request, T)]
      .map{case(request, t) => (request.createRequest(), t)}

    val httpsFlow = Http().cachedHostConnectionPoolHttps[T](RequestCreator.URL)

    val unmarshalFlow = Flow[(Try[HttpResponse], T)].mapAsync(2){
      case (Success(response), t) =>
        //TODO timeout
        response.entity.toStrict(10 seconds)
          .map(s => (s.data.utf8String, t))
      case (Failure(exception), t) =>
        println(s"Failed due to HTTP error")
        throw new Exception(exception)
    }
      .map{case(responseBody, t) =>
        val preprocessed = preprocessBody(responseBody)
        val xml = XML.loadString(preprocessed)
        val modelParse = XmlReader.of[Model].read(xml)
        if(!modelParse.errors.isEmpty) {
          throw new Exception("Parse model have an errors")
        }
        val model = modelParse
          .getOrElse(throw new Exception("Unable to parse"))
        (model, t)
      }

    flow.async.via(httpsFlow).async.via(unmarshalFlow).async
  }
}
