package models.web

import models.clickhouse.TeamRankings
import play.api.libs.json.{Json, OWrites}

case class TeamComparsion(team1Rankings: List[TeamRankings],
                          team2Rankings: List[TeamRankings])

object TeamComparsion {
  implicit val writes: OWrites[TeamComparsion] = Json.writes[TeamComparsion]
}
