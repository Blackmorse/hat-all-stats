package flows

import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object LogProgressFlow {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def apply[I](logEntityName: String, maxProgressFunc: Option[I => Int] = None): Flow[I, I, _] = {
    val accumulateFlow = Flow[I].fold(0)((s, i) => {
      logger.info(s"${s + 1}${maxProgressFunc.map(f => f(i)).map(n => s"/$n").getOrElse("")} $logEntityName loaded")
      s + 1
    })

    val countLoggingSink = Sink.ignore

    Flow.fromGraph{
      GraphDSL.create() {implicit builder =>
        import GraphDSL.Implicits._

        val broadcastCounter = builder.add(Broadcast[I](2))

        broadcastCounter.out(1) ~> accumulateFlow ~> countLoggingSink

        FlowShape(broadcastCounter.in, broadcastCounter.out(0))
      }
    }
  }
}
