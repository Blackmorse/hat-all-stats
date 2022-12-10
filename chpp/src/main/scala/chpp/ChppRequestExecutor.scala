package chpp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.lucidchart.open.xtract.XmlReader

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.xml.XML

object ChppRequestExecutor {
  private val retries = 6
  def execute[Model](request: AbstractRequest[Model], retry: Int = 0)
                    (implicit oauthTokens: OauthTokens, system: ActorSystem, reader: XmlReader[Model]): Future[Model] = {
    import system.dispatcher

    val v = for (response <- Http().singleRequest(request.createRequest());
         responseBody <- response.entity.toStrict(3.minute)) yield {
      val string = responseBody.data.utf8String
      val preprocessed = request.preprocessBody(string)
      val xml = XML.loadString(preprocessed)

      val modelParse = XmlReader.of[Model].read(xml)

      if (modelParse.errors.nonEmpty && retry <= retries) {
        execute(request, retry + 1)
      } else if (modelParse.errors.nonEmpty) {
        throw new Exception(s"It was for request $request. And response was: \n $string \nParse model have an errors: ${modelParse.errors.map(_.toString).mkString("[", ",", "]")}")
      } else {
        modelParse.map(m => Future(m)).getOrElse(throw new Exception("Unable to parse"))
      }
    }

    v.flatten
  }
}
