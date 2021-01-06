package models.clickhouse

import java.util.Date

import chpp.players.models.Player
import models.stream.StreamMatchDetails

case class PlayerInfoModelCH(season: Int,
                            leagueId: Int,
                            divisionLevel: Int,
                            leagueUnitId: Long,
                            leagueUnitName: String,
                            teamId: Long,
                            teamName: String,
                            date: Date,
                            round: Int,
                            matchId: Long,

                            playerId: Long,
                            firstName: String,
                            lastName: String,
                            age: Int,
                            days: Int,
                            roleId: Int,
                            playedMinutes: Int,
                            rating: Int,
                            ratingEndOfMatch: Int,
                            //0 Bruised, -1 no injury
                            injuryLevel: Int,
                            TSI: Int,
                            salary: Int,
                            nationality: Int)

object PlayerInfoModelCH {
  def convert(player: Player, matchDetails: StreamMatchDetails): PlayerInfoModelCH = {
    val (playedMinutes,
        roleId,
        rating,
        ratingEndOfMatch) =
      if(player.lastMatch.date != null && player.lastMatch.date == matchDetails.matc.date) {
        (
          player.lastMatch.playedMinutes,
          player.lastMatch.positionCode.id,
          (player.lastMatch.rating * 10).toInt,
          (player.lastMatch.ratingEndOfMatch * 10).toInt
        ) } else {
          (0, 0, 0, 0)
        }

    PlayerInfoModelCH(
      season = matchDetails.matc.season,
      leagueId = matchDetails.matc.team.leagueUnit.league.leagueId,
      divisionLevel = matchDetails.matc.team.leagueUnit.level,
      leagueUnitId = matchDetails.matc.team.leagueUnit.leagueUnitId,
      leagueUnitName = matchDetails.matc.team.leagueUnit.leagueUnitName,
      teamId = matchDetails.matc.team.id,
      teamName = matchDetails.matc.team.name,
      date = matchDetails.matc.date,
      round = matchDetails.matc.round,
      matchId = matchDetails.matc.id,
      playerId = player.playerPart.playerId,
      firstName = player.playerPart.firstName,
      lastName = player.playerPart.lastName,
      age = player.playerPart.age,
      days = player.playerPart.ageDays,
      nationality = 0, //TODO
      playedMinutes = playedMinutes,
      roleId = roleId,
      rating = rating,
      ratingEndOfMatch = ratingEndOfMatch,
      injuryLevel = player.injuryLevel.getOrElse(-1),
      TSI = player.playerPart.TSI,
      salary = player.playerPart.salary
    )
  }
}
