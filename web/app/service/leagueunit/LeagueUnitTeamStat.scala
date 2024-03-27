package service.leagueunit

import play.api.libs.json.{Json, OWrites}

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
}

case class LeagueUnitTeamStatsWithPositionDiff(positionDiff: Int, leagueUnitTeamStat: LeagueUnitTeamStat)

object LeagueUnitTeamStatsWithPositionDiff {
  implicit val writes: OWrites[LeagueUnitTeamStatsWithPositionDiff] = Json.writes[LeagueUnitTeamStatsWithPositionDiff]
}

case class LeagueUnitTeamStatHistoryInfo(teamsLastRoundWithPositionsDiff: Seq[LeagueUnitTeamStatsWithPositionDiff],
                                         positionsHistory: Seq[LeagueUnitTeamStat])

object LeagueUnitTeamStatHistoryInfo {
  implicit val writes: OWrites[LeagueUnitTeamStatHistoryInfo] = Json.writes[LeagueUnitTeamStatHistoryInfo]
}
