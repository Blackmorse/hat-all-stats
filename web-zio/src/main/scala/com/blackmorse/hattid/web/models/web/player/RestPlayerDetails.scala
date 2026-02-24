package com.blackmorse.hattid.web.models.web.player

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class RestPlayerDetails(playerId: Long,
                             firstName: String,
                             lastName: String,
                             currentPlayerCharacteristics: CurrentPlayerCharacteristics,
                             nativeLeagueId: Int,
                             playerLeagueUnitHistory: List[PlayerLeagueUnitEntry],
                             avatar: Seq[AvatarPart],
                             playerSeasonStats: PlayerSeasonStats,
                             playerCharts: List[PlayerChartEntry]
                            )

case class CurrentPlayerCharacteristics(position: String,
                                        salary: Long,
                                        tsi: Int,
                                        age: Int,
                                        form: Int,
                                        injuryLevel: Int,
                                        experience: Int,
                                        leaderShip: Int,
                                        speciality: Int)

object CurrentPlayerCharacteristics {
  implicit val jsonEncoder: JsonEncoder[CurrentPlayerCharacteristics] = DeriveJsonEncoder.gen[CurrentPlayerCharacteristics]
}

object RestPlayerDetails {
  implicit val jsonEncoder: JsonEncoder[RestPlayerDetails] = DeriveJsonEncoder.gen[RestPlayerDetails]
}

case class AvatarPart(url: String, x: Int, y: Int)

object AvatarPart {
  implicit val jsonEncoder: JsonEncoder[AvatarPart] = DeriveJsonEncoder.gen[AvatarPart]
}

case class PlayerLeagueUnitEntry(season: Int,
                                 round: Int,
                                 fromLeagueId: Int,
                                 fromLeagueUnitId: Int,
                                 fromLeagueUnitName: String,
                                 fromTeamId: Long,
                                 fromTeamName: String,
                                 toLeagueId: Int,
                                 toLeagueUnitId: Int,
                                 toLeagueUnitName: String,
                                 toTeamId: Long,
                                 toTeamName: String,
                                 tsi: Int,
                                 salary: Int,
                                 age: Int)

object PlayerLeagueUnitEntry {
  implicit val jsonEncoder: JsonEncoder[PlayerLeagueUnitEntry] = DeriveJsonEncoder.gen[PlayerLeagueUnitEntry]
}


case class PlayerSeasonStats(entries: List[PlayerSeasonStatsEntry],
                             totalLeagueGoals: Int,
                             totalCupGoals: Int,
                             totalAllGoals: Int,
                             totalYellowCards: Int,
                             totalRedCard: Int,
                             totalMatches: Int,
                             totalPlayedMinutes: Int)

object PlayerSeasonStats {
  implicit val jsonEncoder: JsonEncoder[PlayerSeasonStats] = DeriveJsonEncoder.gen[PlayerSeasonStats]
}

case class PlayerSeasonStatsEntry(season: Int,
                                  leagueGoals: Int,
                                  cupGoals: Int,
                                  allGoals: Int,
                                  yellowCards: Int,
                                  redCards: Int,
                                  matches: Int,
                                  playedMinutes: Int)

object PlayerSeasonStatsEntry {
  implicit val jsonEncoder: JsonEncoder[PlayerSeasonStatsEntry] = DeriveJsonEncoder.gen[PlayerSeasonStatsEntry]
}

case class PlayerChartEntry(age: Int,
                            salary: Int,
                            tsi: Long,
                            rating: Int,
                            ratingEndOfMatch: Int)

object PlayerChartEntry {
  implicit val jsonEncoder: JsonEncoder[PlayerChartEntry] = DeriveJsonEncoder.gen[PlayerChartEntry]
}

