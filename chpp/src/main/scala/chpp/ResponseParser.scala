package chpp

import chpp.ChppRequestExecutor.{ChppErrorResponse, ChppUnparsableErrorResponse, ModelUnparsableResponse}
import chpp.chpperror.ChppError
import com.lucidchart.open.xtract.XmlReader

import scala.xml.{Elem, XML}

object ResponseParser {
  def parseResponse[Model](request: AbstractRequest[Model], rawResponse: String)(implicit reader: XmlReader[Model]): Model = {
    val preprocessedBody = request.preprocessResponseBody(rawResponse)
    val xml = XML.loadString(preprocessedBody)

    val errorResponse = xml.child
      .find(node => node.label == "FileName" && node.text == "chpperror.xml")

    errorResponse.foreach(_ => parseError(xml, rawResponse))

    parseModel(request, xml, rawResponse)
  }

  // Throwing error for akka retries
  private def parseError(xml: Elem, rawResponse: String): Unit = {
    val parse = XmlReader.of[ChppError].read(xml)
    if (parse.errors.nonEmpty) {
      throw ChppUnparsableErrorResponse(parse.errors, rawResponse)
    }

    val chppError = parse.getOrElse(throw new Exception(s"Unknown error for response: $rawResponse"))
    throw ChppErrorResponse(chppError)
  }

  private def parseModel[Model](request: AbstractRequest[Model], xml: Elem, rawResponse: String)(implicit reader: XmlReader[Model]): Model = {
    val modelParse = XmlReader.of[Model].read(xml)

    if (modelParse.errors.nonEmpty) {
      throw ModelUnparsableResponse(modelParse.errors, rawResponse, request.toString)
    } else {
      modelParse.getOrElse(throw new Exception(s"Unable to parse model with $rawResponse. Request: $request"))
    }
  }
}
