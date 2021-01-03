package flows

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import models.OauthTokens
import models.chpp.search.Search
import models.chpp.worlddetails.WorldDetails
import requests.{SearchRequest, WorldDetailsRequest}

import scala.concurrent.ExecutionContext

object SearchFlow extends AbstractFlow[SearchRequest, Search] {
//  def create[T](implicit oauthTokens: OauthTokens, system: ActorSystem, executionContext: ExecutionContext): Flow[(SearchRequest, T), (Search, T), NotUsed] = {
//
//  }
  override def preprocessBody(body: String): String =
  body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
