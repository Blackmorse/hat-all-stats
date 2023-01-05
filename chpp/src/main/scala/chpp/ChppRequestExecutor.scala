package chpp

import akka.actor.{ActorSystem, Scheduler}
import akka.http.scaladsl.Http
import chpp.chpperror.ChppError
import com.lucidchart.open.xtract.{ParseError, XmlReader}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML}

object ChppRequestExecutor {
  private val retries = 4

  case class ChppErrorResponse(chppError: ChppError) extends Exception
  case class ChppUnparsableErrorResponse(errors: Seq[ParseError], rawResponse: String) extends Exception {
    override def toString: String = {
      s"$errors \n rawResponse: $rawResponse"
    }
  }

  case class ModelUnparsableResponse(errors: Seq[ParseError], rawResponse: String, request: String) extends Exception {
    override def toString: String = {
      errors.mkString(", ") + "\n " + rawResponse + s"\n request: $request"
    }
  }

  def executeWithRetry[Model](request: AbstractRequest[Model])
                      (implicit oauthTokens: OauthTokens, system: ActorSystem, reader: XmlReader[Model]): Future[Either[ChppError, Model]] = {
    import system.dispatcher
    implicit val scheduler: Scheduler =  system.scheduler
    //Throwing exceptions insides to enable retries, as it triggered by throwing exceptions
    akka.pattern.retry(
      attempt = () => execute(request),
      attempts = retries,
      minBackoff = 300.millisecond,
      maxBackoff = 2.seconds,
      randomFactor = 0.3
    ) transform {
      case Success(model) => Try(Right(model))
      case Failure(ChppErrorResponse(chppError)) => Try(Left(chppError))
      case Failure(e) => Try(throw e)
    }
  }

  private def execute[Model](request: AbstractRequest[Model])
                       (implicit oauthTokens: OauthTokens, system: ActorSystem, reader: XmlReader[Model]): Future[Model] = {
    import system.dispatcher
    for (response <- Http().singleRequest(request.createRequest());
                 responseBody <- response.entity.toStrict(3.minute)) yield {
      val rawResponse = responseBody.data.utf8String
      val preprocessed = request.preprocessResponseBody(rawResponse)
      val xml = XML.loadString(preprocessed)

      val errorResponse = xml.child
        .find(node => node.label == "FileName" && node.text == "chpperror.xml")

      errorResponse.foreach(_ => parseError(xml, rawResponse))

      parseModel(request, xml, rawResponse)
    }
  }

  private def parseError(xml: Elem, rawResponse: String): Try[ChppError] = {
    val parse = XmlReader.of[ChppError].read(xml)
    if (parse.errors.nonEmpty) {
      throw ChppUnparsableErrorResponse(parse.errors, rawResponse)
    }

    val chppError = parse.getOrElse(throw new Exception(s"Unknown error for response: $rawResponse"))
    throw ChppErrorResponse(chppError)
  }

  private def parseModel[Model](request: AbstractRequest[Model],xml: Elem, rawResponse: String)(implicit reader: XmlReader[Model]): Model = {
    val modelParse = XmlReader.of[Model].read(xml)

    if (modelParse.errors.nonEmpty) {
      throw ModelUnparsableResponse(modelParse.errors, rawResponse, request.toString)
    } else {

      modelParse.getOrElse(throw new Exception(s"Unable to parse model with $rawResponse. Request: $request"))
    }
  }
}
