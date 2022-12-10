package chpp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import chpp.chpperror.ChppError
import com.lucidchart.open.xtract.XmlReader

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML}

object ChppRequestExecutor {
  private val retries = 4

  def execute[Model](request: AbstractRequest[Model], retry: Int = 0)
                    (implicit oauthTokens: OauthTokens, system: ActorSystem, reader: XmlReader[Model]): Future[Either[ChppError, Model]] = {
    import system.dispatcher
    val v = for (response <- Http().singleRequest(request.createRequest());
         responseBody <- response.entity.toStrict(3.minute)) yield {

      val rawResponse = responseBody.data.utf8String
      val preprocessed = request.preprocessBody(rawResponse)
      val xml = XML.loadString(preprocessed)
      val errorResponse = xml.child
        .find(node => node.label == "FileName" && node.text == "chpperror.xml")

      val responseEither = errorResponse match {
        case Some(_) => Left(parseError(xml, rawResponse))
        case None => Right(parseModel(xml, rawResponse))
      }

      responseEither match {
        case Right(Success(model)) => Future(Right(model))
        case _ if retry <= retries => execute(request, retry + 1)
        case v => Future(eitherTriesToTryEither(v).get)
      }
    }

    v.flatten
  }

  private def parseError(xml: Elem, rawResponse: String): Try[ChppError] = Try {
    val parse = XmlReader.of[ChppError].read(xml)
    if (parse.errors.nonEmpty) {
      throw new Exception(s"Unable to parse error response. \n " +
        s"ParseErrors: ${parse.errors.map(_.toString).mkString("[", ",", "]")} \n" +
        s"Response: $rawResponse")
    }

    parse.getOrElse(throw new Exception(s"Unknown error for response: $rawResponse"))
  }

  private def parseModel[Model](xml: Elem, rawResponse: String)(implicit reader: XmlReader[Model]): Try[Model] = Try {
    val modelParse = XmlReader.of[Model].read(xml)

    if (modelParse.errors.nonEmpty) {
      throw new Exception(s"Unable to parse model response. \nResponse was: \n $rawResponse \nParse model have an errors: ${modelParse.errors.map(_.toString).mkString("[", ",", "]")}")
    } else {
      modelParse.getOrElse(throw new Exception(s"Unable to parse $rawResponse"))
    }
  }

  private def eitherTriesToTryEither[A,B](lr: Either[Try[A], Try[B]]): Try[Either[A, B]] = lr match {
    case Left(Success(value)) => Success(Left(value))
    case Left(Failure(exception)) => Failure(exception)
    case Right(Success(value)) => Success(Right(value))
    case Right(Failure(exception)) => Failure(exception)
  }
}
