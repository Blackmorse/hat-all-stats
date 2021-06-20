package flows

import chpp.players.PlayersRequest
import chpp.players.models.Players

object PlayersHttpFlow extends AbstractHttpFlow[PlayersRequest, Players] {
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
