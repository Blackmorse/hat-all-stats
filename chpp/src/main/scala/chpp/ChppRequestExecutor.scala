package chpp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.lucidchart.open.xtract.XmlReader

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.xml.XML

object ChppRequestExecutor {
  def execute[Model](request: AbstractRequest[Model])
                    (implicit oauthTokens: OauthTokens, system: ActorSystem, reader: XmlReader[Model]): Future[Model] = {
    import system.dispatcher

    for (response <- Http().singleRequest(request.createRequest());
         responseBody <- response.entity.toStrict(1.minute)) yield {
      val preprocessed = request.preprocessBody(responseBody.data.utf8String)
      val xml = XML.loadString(preprocessed)

      val modelParse = XmlReader.of[Model].read(xml)

      if(modelParse.errors.nonEmpty) {
        throw new Exception(s"Parse model have an errors: ${modelParse.errors.map(_.toString).mkString("[", ",", "]")}")
      }
      modelParse.getOrElse(throw new Exception("Unable to parse"))
    }
  }
}
