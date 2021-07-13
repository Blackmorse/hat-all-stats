package models.clickhouse

import java.util.Date

import play.api.libs.json.Json

case class NearestMatch(matchDate: Date,
                        status: String,
                        homeTeamId: Long,
                        homeTeamName: String,
                        homeGoals: Option[Int],
                        awayGoals: Option[Int],
                        awayTeamName: String,
                        awayTeamId: Long,
                        matchId: Long)

object NearestMatch {
  implicit val writes = Json.writes[NearestMatch]
}
