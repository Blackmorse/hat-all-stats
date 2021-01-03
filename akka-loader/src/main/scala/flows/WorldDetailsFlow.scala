package flows

import models.chpp.worlddetails.WorldDetails
import requests.WorldDetailsRequest

object WorldDetailsFlow extends AbstractFlow[WorldDetailsRequest, WorldDetails] {
  override def preprocessBody(body: String): String =
        body.replace("<Country Available=False />", "")
            .replace("Available=True", "Available=\"True\"")
            .replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
