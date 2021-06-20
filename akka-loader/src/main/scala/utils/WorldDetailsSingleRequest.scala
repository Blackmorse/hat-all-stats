package utils

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import chpp.OauthTokens
import chpp.worlddetails.models.WorldDetails
import chpp.worlddetails.WorldDetailsRequest
import flows.WorldDetailsHttpFlow

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object WorldDetailsSingleRequest {
  def request(leagueId: Option[Int] = None)(implicit oauthTokens: OauthTokens,
                actorSystem: ActorSystem,
                executionContext: ExecutionContext): Future[WorldDetails] = {
    Source.single((WorldDetailsRequest(leagueId = leagueId), ()))
      .via(WorldDetailsHttpFlow())
      .map(_._1)
      .runFold(null.asInstanceOf[WorldDetails])((_, wd) => wd)
  }
}
