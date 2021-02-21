package utils

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import chpp.OauthTokens
import chpp.worlddetails.models.WorldDetails
import chpp.worlddetails.{WorldDetailsHttpFlow, WorldDetailsRequest}
import scala.concurrent.duration._

import scala.concurrent.{Await, ExecutionContext}

object WorldDetailsSingleRequest {
  def request(leagueId: Option[Int] = None)(implicit oauthTokens: OauthTokens,
                actorSystem: ActorSystem,
                executionContext: ExecutionContext): WorldDetails = {
    val future = Source.single((WorldDetailsRequest(leagueId = leagueId), Unit))
      .via(WorldDetailsHttpFlow())
      .map(_._1)
      .runFold(null.asInstanceOf[WorldDetails])((_, wd) => wd)

    Await.result(future, 30 seconds)
  }
}
