package flows.http

import flows.AbstractHttpFlow
import models.chpp.players.Players
import requests.PlayersRequest

object PlayersFlow extends AbstractHttpFlow[PlayersRequest, Players]{
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
