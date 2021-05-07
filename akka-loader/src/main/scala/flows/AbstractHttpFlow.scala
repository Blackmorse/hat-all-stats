package flows

import akka.NotUsed
import akka.actor.ActorSystem
import akka.dispatch.MessageDispatcher
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL}
import chpp.{AbstractRequest, OauthTokens, RequestCreator}
import com.lucidchart.open.xtract.XmlReader
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import scala.xml.XML

abstract class AbstractHttpFlow[Request <: AbstractRequest, Model] {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def preprocessBody(body: String): String

  def apply[T]()(implicit oauthTokens: OauthTokens, system: ActorSystem,
                reader: XmlReader[Model]): Flow[(Request, T), (Model, T), NotUsed] = {

//    implicit val dispatcher: MessageDispatcher = system.dispatchers.lookup("my-dispatcher")
    import system.dispatcher
    val flow = Flow[(Request, T)]
      .map{case(request, t) => (request.createRequest(), t)}.async

//    val httpsFlow = Http().cachedHostConnectionPoolHttps[T](RequestCreator.URL)

    val httpsFlow2 = flow.mapAsyncUnordered(32){
      case(request, t) =>
        val resp = Http().singleRequest(request)//.map(resp => (resp, t))

//      }
//      .mapAsync(2){
        resp.flatMap(response => {
          val r = response.entity.toStrict(1.minute)
          r.map(res => (res.data.utf8String, t))
        })

//        case(response, t) =>
//          val r = response.entity.toStrict(1.minute)
//          r.map(res => (res.data.utf8String, t))
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

//    val unmarshalFlow = Flow[(Try[HttpResponse], T)]
//      .mapAsync(2){
//      case (Success(response), t) =>
//        //TODO timeout
//        val r = Unmarshal(response.entity).to[String]
//        r.map((_, t))
////        response.entity.dataBytes
////          .runReduce(_ ++ _)
////          .map(data => (data.utf8String, t))
//      case (Failure(f), t) =>
//        Future(("", t))
//    }
//      .map{case(responseBody, t) =>
//        val preprocessed = preprocessBody(responseBody)
//        val xml = XML.loadString(preprocessed)
//        val modelParse = XmlReader.of[Model].read(xml)
//        if(!modelParse.errors.isEmpty) {
//          throw new Exception("Parse model have an errors")
//        }
//        val model = modelParse
//          .getOrElse(throw new Exception("Unable to parse"))
//        (model, t)
//      }


//    flow.async.via(httpsFlow).async.via(unmarshalFlow).async
    httpsFlow2.async
  }
}
