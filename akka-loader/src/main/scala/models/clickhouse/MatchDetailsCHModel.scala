package models.clickhouse

import java.util.Date

import models.stream.StreamMatchDetails

object IsHomeMatch extends Enumeration {
  val HOME = Value(1, "home")
  val AWAY = Value(0, "away")
}

case class MatchDetailsCHModel(season: Int,
                              leagueId: Int,
                              divisionLevel: Int,
                              leagueUnitId: Int,
                              leagueUnitName: String,
                              teamId: Long,
                              teamName: String,
                              date: Date,
                              round: Int,
                              matchId: Long,

                              isHomeMatch: IsHomeMatch.Value,
                              goals: Int,
                              oppositeGoals: Int,

                              soldTotal: Int,

                              formation: String,
                              tacticType: Int,
                              tacticSkill: Int,
                              ratingMidfield: Int,
                              ratingRightDef: Int,
                              ratingLeftDef: Int,
                              ratingMidDef: Int,
                              ratingRightAtt: Int,
                              ratingMidAtt: Int,
                              ratingLeftAtt: Int,
                              ratingIndirectSetPiecesDef: Int,
                              ratingIndirectSetPiecesAtt: Int,

                              oppositeTeamId: Long,
                              oppositeTeamName: String,
                              oppositeFormation: String,
                              oppositeTacticType: Int,
                              oppositeTacticSkill: Int,
                              oppositeRatingMidfield: Int,
                              oppositeRatingRightDef: Int,
                              oppositeRatingLeftDef: Int,
                              oppositeRatingMidDef: Int,
                              oppositeRatingRightAtt: Int,
                              oppositeRatingMidAtt: Int,
                              oppositeRatingLeftAtt: Int,
                              oppositeRatingIndirectSetPiecesDef: Int,
                              oppositeRatingIndirectSetPiecesAtt: Int)

object MatchDetailsCHModel {
  def convert(streamMatchDetails: StreamMatchDetails): MatchDetailsCHModel = {
    val matchDetails = streamMatchDetails.matchDetails

    val homeTeamId = matchDetails.matc.homeTeam.teamId

    val (isHomeMatch,
          currentTeam,
          oppositeTeam) =
      if(homeTeamId == streamMatchDetails.matc.team.id) {
        (IsHomeMatch.HOME,
          matchDetails.matc.homeTeam,
          matchDetails.matc.awayTeam)
      } else {
        (IsHomeMatch.AWAY,
          matchDetails.matc.awayTeam,
          matchDetails.matc.homeTeam)
      }

    MatchDetailsCHModel(
      season = streamMatchDetails.matc.season,
      leagueId = streamMatchDetails.matc.team.leagueUnit.league.leagueId,
      divisionLevel = streamMatchDetails.matc.team.leagueUnit.level,
      leagueUnitId = streamMatchDetails.matc.team.leagueUnit.leagueUnitId,
      leagueUnitName = streamMatchDetails.matc.team.leagueUnit.leagueUnitName,
      teamId = streamMatchDetails.matc.team.id,
      teamName = streamMatchDetails.matc.team.name,
      date = streamMatchDetails.matc.date,
      round = streamMatchDetails.matc.round,
      matchId = streamMatchDetails.matc.id,

      isHomeMatch = isHomeMatch,
      goals = currentTeam.goals,
      oppositeGoals = oppositeTeam.goals,

      soldTotal = matchDetails.matc.arena.soldTotal,

      formation = currentTeam.formation,
      tacticType = currentTeam.tacticType,
      tacticSkill = currentTeam.tacticSkill,
      ratingMidfield = currentTeam.ratingMidfied,
      ratingLeftDef = currentTeam.ratingLeftDef,
      ratingMidDef = currentTeam.ratingMidDef,
      ratingRightDef = currentTeam.ratingRightDef,
      ratingLeftAtt = currentTeam.ratingLeftAtt,
      ratingMidAtt = currentTeam.ratingMidAtt,
      ratingRightAtt = currentTeam.ratingRightAtt,
      ratingIndirectSetPiecesDef = currentTeam.ratingIndirectSetPiecesDef,
      ratingIndirectSetPiecesAtt = currentTeam.ratingInderectSetPiecesAtt,

      oppositeTeamId = oppositeTeam.teamId,
      oppositeTeamName = oppositeTeam.teamName,
      oppositeFormation = oppositeTeam.formation,
      oppositeTacticType = oppositeTeam.tacticType,
      oppositeTacticSkill = oppositeTeam.tacticSkill,
      oppositeRatingMidfield = oppositeTeam.ratingMidfied,
      oppositeRatingLeftDef = oppositeTeam.ratingLeftDef,
      oppositeRatingMidDef = oppositeTeam.ratingMidDef,
      oppositeRatingRightDef = oppositeTeam.ratingRightDef,
      oppositeRatingLeftAtt = oppositeTeam.ratingLeftAtt,
      oppositeRatingMidAtt = oppositeTeam.ratingMidAtt,
      oppositeRatingRightAtt = oppositeTeam.ratingRightAtt,
      oppositeRatingIndirectSetPiecesDef = oppositeTeam.ratingIndirectSetPiecesDef,
      oppositeRatingIndirectSetPiecesAtt = oppositeTeam.ratingInderectSetPiecesAtt
    )
  }
}
