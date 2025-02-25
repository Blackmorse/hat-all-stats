package httpflows

import org.apache.pekko.NotUsed
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.Flow
import chpp.{AbstractRequest, ChppRequestExecutor, OauthTokens}
import com.lucidchart.open.xtract.XmlReader
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory


abstract class AbstractHttpFlow[Request <: AbstractRequest[Model], Model] {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def apply[T]()(implicit oauthTokens: OauthTokens, system: ActorSystem,
                reader: XmlReader[Model]): Flow[(Request, T), (Model, T), NotUsed] = {

    import system.dispatcher
    Flow[(Request, T)]
      .mapAsyncUnordered(2) {
        case (request, t) =>
          ChppRequestExecutor.executeWithRetry(request)
            .recover {
              case e: Exception =>
                logger.error(e.getMessage, e)
                throw e
            }
            .map{
              case Right(value) => value
              case Left(value) =>
                logger.error(s"Error at ChppRequestExecutor: $value")
                throw new Exception(s"Error response: $value")
            }
            .map(r => (r, t))
      }.async
  }
}
