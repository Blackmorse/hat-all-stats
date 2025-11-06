package models.web.teams

import models.clickhouse.TeamRankings
import play.api.libs.json.{Json, OWrites}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

object RestTeamRankings {
  implicit val writes: OWrites[RestTeamRankings] = Json.writes[RestTeamRankings]
  implicit val jsonEncoder: JsonEncoder[RestTeamRankings] = DeriveJsonEncoder.gen[RestTeamRankings]
}

case class RestTeamRankings(teamRankings: Seq[TeamRankings],
                            leagueTeamsCounts: Seq[(Int, Long)],
                            divisionLevelTeamsCounts: Seq[(Int, Long)],
                            currencyRate: Double,
                            currencyName: String)
