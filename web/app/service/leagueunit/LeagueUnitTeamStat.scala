package service.leagueunit

import play.api.libs.json.{Json, OWrites}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class LeagueUnitTeamStat(round: Int,
                              position: Int,
                              teamId: Long,
                              teamName: String,
                              games: Int,
                              scored: Int,
                              missed: Int,
                              win: Int,
                              draw: Int,
                              lost: Int,
                              points: Int)

object LeagueUnitTeamStat {
  implicit val writes: OWrites[LeagueUnitTeamStat] = Json.writes[LeagueUnitTeamStat]
  implicit val jsonEncoder: JsonEncoder[LeagueUnitTeamStat] = DeriveJsonEncoder.gen[LeagueUnitTeamStat]
}

case class LeagueUnitTeamStatsWithPositionDiff(positionDiff: Int,
                                               leagueUnitTeamStat: LeagueUnitTeamStat)

object LeagueUnitTeamStatsWithPositionDiff {
  implicit val writes: OWrites[LeagueUnitTeamStatsWithPositionDiff] = Json.writes[LeagueUnitTeamStatsWithPositionDiff]
  implicit val jsonEncoder: JsonEncoder[LeagueUnitTeamStatsWithPositionDiff] = DeriveJsonEncoder.gen[LeagueUnitTeamStatsWithPositionDiff]
}

case class LeagueUnitTeamStatHistoryInfo(teamsLastRoundWithPositionsDiff: Seq[LeagueUnitTeamStatsWithPositionDiff],
                                         positionsHistory: Seq[LeagueUnitTeamStat])

object LeagueUnitTeamStatHistoryInfo {
  implicit val writes: OWrites[LeagueUnitTeamStatHistoryInfo] = Json.writes[LeagueUnitTeamStatHistoryInfo]
  implicit val jsonEncoder: JsonEncoder[LeagueUnitTeamStatHistoryInfo] = DeriveJsonEncoder.gen[LeagueUnitTeamStatHistoryInfo]
}
