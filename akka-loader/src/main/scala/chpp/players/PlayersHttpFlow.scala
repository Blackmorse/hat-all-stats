package chpp.players

import chpp.players.models.Players
import flows.AbstractHttpFlow

object PlayersHttpFlow extends AbstractHttpFlow[PlayersRequest, Players]{
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
