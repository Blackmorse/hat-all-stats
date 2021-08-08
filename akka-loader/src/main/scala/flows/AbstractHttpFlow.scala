package flows

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import chpp.{AbstractRequest, ChppRequestExecutor, OauthTokens}
import com.lucidchart.open.xtract.XmlReader


abstract class AbstractHttpFlow[Request <: AbstractRequest[Model], Model] {

  def apply[T]()(implicit oauthTokens: OauthTokens, system: ActorSystem,
                reader: XmlReader[Model]): Flow[(Request, T), (Model, T), NotUsed] = {

    import system.dispatcher
    Flow[(Request, T)]
      .mapAsyncUnordered(32) {
        case (request, t) =>
          ChppRequestExecutor.execute(request).map((_, t))
      }.async
  }
}
