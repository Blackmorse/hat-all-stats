package flows

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL}
import com.lucidchart.open.xtract.XmlReader
import models.OauthTokens
import requests.AbstractRequest

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Success, Try}
import scala.xml.XML

abstract class AbstractHttpFlow[Request <: AbstractRequest, Model] {
  def preprocessBody(body: String): String

  def apply[T]()(implicit oauthTokens: OauthTokens, system: ActorSystem,
               executionContext: ExecutionContext, reader: XmlReader[Model]): Flow[(Request, T), (Model, T), NotUsed] = {
    val flow = Flow[(Request, T)]
      .map{case(request, t) => (request.createRequest(), t)}

    val httpsFlow = Http().cachedHostConnectionPoolHttps[T]("chpp.hattrick.org")

    val unmarshalFlow = Flow[(Try[HttpResponse], T)].mapAsync(2){
      case (Success(response), t) =>
        //TODO timeout
        response.entity.toStrict(10 seconds)
          .map(s => (s.data.utf8String, t))

    }
      .map{case(responseBody, t) =>
        val preprocessed = preprocessBody(responseBody)
        val xml = XML.loadString(preprocessed)
        val model = XmlReader.of[Model].read(xml)
        (model
          .getOrElse(throw new RuntimeException("Unable to parse")), t)
      }

    Flow.fromGraph{
      GraphDSL.create(){ implicit builder =>
        import GraphDSL.Implicits._

        val toRequestConverterFlowShape = builder.add(flow)
        val httpRequestFlowShape = builder.add(httpsFlow)
        val unmarshallFlowShape = builder.add(unmarshalFlow)

        toRequestConverterFlowShape ~> httpRequestFlowShape ~> unmarshallFlowShape

        FlowShape(toRequestConverterFlowShape.in, unmarshallFlowShape.out)
      }
    }
  }
}
