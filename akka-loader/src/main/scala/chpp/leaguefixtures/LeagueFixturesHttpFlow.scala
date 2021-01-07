package chpp.leaguefixtures

import chpp.leaguefixtures.models.LeagueFixtures
import flows.AbstractHttpFlow

object LeagueFixturesHttpFlow extends AbstractHttpFlow[LeagueFixturesRequest, LeagueFixtures] {
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
