package models.clickhouse

import chpp.matches.models.Match

import java.util.Date
import play.api.libs.json.{Json, OWrites}

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
  implicit val writes: OWrites[NearestMatch] = Json.writes[NearestMatch]

  def chppMatchToNearestMatch(matc: Match): NearestMatch =
    NearestMatch(
      matchDate = matc.matchDate,
      status = matc.status,
      homeTeamId = matc.homeTeam.homeTeamId,
      homeTeamName = matc.homeTeam.homeTeamName,
      homeGoals = matc.homeGoals,
      awayGoals = matc.awayGoals,
      awayTeamName = matc.awayTeam.awayTeamName,
      awayTeamId = matc.awayTeam.awayTeamId,
      matchId = matc.matchId)
}
