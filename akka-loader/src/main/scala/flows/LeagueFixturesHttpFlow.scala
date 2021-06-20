package flows

import chpp.leaguefixtures.LeagueFixturesRequest
import chpp.leaguefixtures.models.LeagueFixtures

object LeagueFixturesHttpFlow extends AbstractHttpFlow[LeagueFixturesRequest, LeagueFixtures] {
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
