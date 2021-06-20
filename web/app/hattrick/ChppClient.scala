package hattrick

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import chpp.{AbstractRequest, OauthTokens}
import com.lucidchart.open.xtract.XmlReader
import play.api.Configuration

import scala.concurrent.duration._
import javax.inject.{Inject, Singleton}
import scala.xml.XML
import play.api.Logger

import scala.concurrent.Future

@Singleton
class ChppClient @Inject()(val configuration: Configuration,
                            implicit val actorSystem: ActorSystem
                          ) {
  val logger: Logger = Logger(this.getClass)

  import actorSystem.dispatcher

  private implicit val oauthTokens = OauthTokens(configuration.get[String]("hattrick.accessToken"),
    configuration.get[String]("hattrick.customerKey"),
    configuration.get[String]("hattrick.customerSecret"),
    configuration.get[String]("hattrick.accessTokenSecret")
    )

  def execute[Model, Request <: AbstractRequest[Model]](request: Request)(implicit reader: XmlReader[Model]): Future[Model] = {
    Http().singleRequest(request.createRequest())
      .flatMap(response => {
        response.entity.toStrict(1.minute).map(res => res.data.utf8String)
      })
      .map(responseBody => {
        val preprocessed = request.preprocessBody(responseBody)
        val xml = XML.loadString(preprocessed)
        val modelParse = XmlReader.of[Model].read(xml)

        if(!modelParse.errors.isEmpty) {
          modelParse.errors.foreach(pe => logger.error(pe.toString))
          throw new Exception("Parse model have an errors")
        }
        modelParse.getOrElse(throw new Exception("Unable to parse"))
      })

  }
}
