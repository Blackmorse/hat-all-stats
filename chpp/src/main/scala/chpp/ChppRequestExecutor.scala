package chpp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.lucidchart.open.xtract.XmlReader

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.xml.XML

object ChppRequestExecutor {
  private val retries = 3
  def execute[Model](request: AbstractRequest[Model], retry: Int = 0)
                    (implicit oauthTokens: OauthTokens, system: ActorSystem, reader: XmlReader[Model]): Future[Model] = {
    import system.dispatcher

    for (response <- Http().singleRequest(request.createRequest());
         responseBody <- response.entity.toStrict(1.minute)) yield {
      val string = responseBody.data.utf8String
      val preprocessed = request.preprocessBody(string)
      val xml = XML.loadString(preprocessed)

      val modelParse = XmlReader.of[Model].read(xml)

      if (modelParse.errors.nonEmpty && retry <= retries) {
        Await.result(execute(request, retry + 1), ((retry) * 4).seconds)
      } else if (modelParse.errors.nonEmpty) {
        throw new Exception(s"It was for request $request. And response was: \n $string \nParse model have an errors: ${modelParse.errors.map(_.toString).mkString("[", ",", "]")}")
      } else {
        modelParse.getOrElse(throw new Exception("Unable to parse"))
      }
    }
  }
}
