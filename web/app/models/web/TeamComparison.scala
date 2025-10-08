package models.web

import models.clickhouse.TeamRankings
import play.api.libs.json.{Json, OWrites}

case class TeamComparison(team1Rankings: List[TeamRankings],
                          team2Rankings: List[TeamRankings])

object TeamComparison {
  implicit val writes: OWrites[TeamComparison] = Json.writes[TeamComparison]
  
  def empty(): TeamComparison = TeamComparison(List(), List())
}
