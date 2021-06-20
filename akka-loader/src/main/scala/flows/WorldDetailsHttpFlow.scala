package flows

import chpp.worlddetails.WorldDetailsRequest
import chpp.worlddetails.models.WorldDetails

object WorldDetailsHttpFlow extends AbstractHttpFlow[WorldDetailsRequest, WorldDetails] {
  override def preprocessBody(body: String): String =
    body.replace("<Country Available=False />", "")
      .replace("Available=True", "Available=\"True\"")
      .replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
