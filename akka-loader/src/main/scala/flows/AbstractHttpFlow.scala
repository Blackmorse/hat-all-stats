package flows

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.scaladsl.Flow
import chpp.{AbstractRequest, OauthTokens}
import com.lucidchart.open.xtract.XmlReader
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.xml.XML

abstract class AbstractHttpFlow[Request <: AbstractRequest[Model], Model] {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def preprocessBody(body: String): String

  def apply[T]()(implicit oauthTokens: OauthTokens, system: ActorSystem,
                reader: XmlReader[Model]): Flow[(Request, T), (Model, T), NotUsed] = {

    import system.dispatcher
    val flow = Flow[(Request, T)]
      .map{case(request, t) => (request.createRequest(), t)}.async

    val httpsFlow2 = flow.mapAsyncUnordered(32){
      case(request, t) =>
        val resp = Http().singleRequest(request)

        resp.flatMap(response => {
          val r = response.entity.toStrict(1.minute)
          r.map(res => (res.data.utf8String, t))
        })

      }
      .map{case(responseBody, t) =>
        val preprocessed = preprocessBody(responseBody)
        val xml = XML.loadString(preprocessed)
        val modelParse = XmlReader.of[Model].read(xml)
        if(!modelParse.errors.isEmpty) {
          modelParse.errors.foreach(pe => logger.error(pe.toString))
          throw new Exception("Parse model have an errors")
        }
        val model = modelParse
          .getOrElse(throw new Exception("Unable to parse"))
        (model, t)
      }
    httpsFlow2.async
  }
}
