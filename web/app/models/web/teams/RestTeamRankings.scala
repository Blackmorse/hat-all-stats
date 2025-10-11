package models.web.teams

import models.clickhouse.TeamRankings
import play.api.libs.json.{Json, OWrites}

object RestTeamRankings {
  implicit val writes: OWrites[RestTeamRankings] = Json.writes[RestTeamRankings]
}

case class RestTeamRankings(teamRankings: Seq[TeamRankings],
                            leagueTeamsCounts: Seq[(Int, Long)],
                            divisionLevelTeamsCounts: Seq[(Int, Long)],
                            currencyRate: Double,
                            currencyName: String)
