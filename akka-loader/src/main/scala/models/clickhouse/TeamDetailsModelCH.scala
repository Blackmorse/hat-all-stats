package models.clickhouse

import chpp.teamdetails.models.{Team, TrophyTypeId}
import models.stream.StreamMatchDetails

case class TeamDetailsModelCH(season: Int,
                             leagueId: Int,
                             divisionLevel: Int,
                             leagueUnitId: Long,
                             leagueUnitName: String,
                             teamId: Long,
                             teamName: String,
                             round: Int,

                             powerRating: Int,
                             homeFlags: Int,
                             awayFlags: Int,
                             fanclubSize: Int,
                             trophiesNumber: Int,
                             numberOfVictories: Int,
                             numberOfUndefeated: Int)

object TeamDetailsModelCH {
  def convert(team: Team, matchDetails: StreamMatchDetails): TeamDetailsModelCH = {
    val trophyNumber = team.trophyList.count(trophy => trophy.trophyTypeId != TrophyTypeId.TOURNAMENT_WINNER
                                                    && trophy.trophyTypeId != TrophyTypeId.STUDY_TOURNNAMENT)

    TeamDetailsModelCH(
      season = matchDetails.matc.season,
      leagueId = matchDetails.matc.team.leagueUnit.league.leagueId,
      divisionLevel = matchDetails.matc.team.leagueUnit.level,
      leagueUnitId = matchDetails.matc.team.leagueUnit.leagueUnitId,
      leagueUnitName = matchDetails.matc.team.leagueUnit.leagueUnitName,
      teamId = team.teamId,
      teamName = team.teamName,
      round = matchDetails.matc.round,
      powerRating = team.powerRating.powerRating,
      homeFlags = team.flags.homeFlags.size,
      awayFlags = team.flags.awaFlags.size,
      fanclubSize = team.fanclub.fanclubSize,
      trophiesNumber = trophyNumber,
      numberOfVictories = team.numberOfVictories,
      numberOfUndefeated = team.numberOfUndefeated
    )
  }
}