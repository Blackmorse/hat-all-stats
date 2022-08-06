package models.web.player

import play.api.libs.json.{Json, OWrites}

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
  implicit val writes: OWrites[CurrentPlayerCharacteristics] = Json.writes[CurrentPlayerCharacteristics]
}

object RestPlayerDetails {
  implicit val writes: OWrites[RestPlayerDetails] = Json.writes[RestPlayerDetails]
}

case class AvatarPart(url: String, x: Int, y: Int)

object AvatarPart {
  implicit val writes: OWrites[AvatarPart] = Json.writes[AvatarPart]
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
  implicit val writes: OWrites[PlayerLeagueUnitEntry] = Json.writes[PlayerLeagueUnitEntry]
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
  implicit val writes: OWrites[PlayerSeasonStats] = Json.writes[PlayerSeasonStats]
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
  implicit val writes: OWrites[PlayerSeasonStatsEntry] = Json.writes[PlayerSeasonStatsEntry]
}

case class PlayerChartEntry(age: Int,
                            salary: Int,
                            tsi: Long,
                            rating: Int,
                            ratingEndOfMatch: Int)

object PlayerChartEntry {
  implicit val writes: OWrites[PlayerChartEntry] = Json.writes[PlayerChartEntry]
}

