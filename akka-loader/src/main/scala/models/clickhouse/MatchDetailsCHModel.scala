package models.clickhouse

import java.util.Date

import models.stream.StreamMatchDetails
import spray.json.{JsNumber, JsObject, JsString, JsValue, JsonFormat}
import utils.DateTimeMarshalling._

object IsHomeMatch extends Enumeration {
  val HOME = Value(0, "home")
  val AWAY = Value(1, "away")
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
                               enemyGoals: Int,

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
  implicit val format: JsonFormat[MatchDetailsCHModel] = new JsonFormat[MatchDetailsCHModel] {
    override def read(json: JsValue): MatchDetailsCHModel = null

    override def write(obj: MatchDetailsCHModel): JsValue = {
      JsObject(
        ("season", JsNumber(obj.season)),
        ("league_id", JsNumber(obj.leagueId)),
        ("division_level", JsNumber(obj.divisionLevel)),
        ("league_unit_id", JsNumber(obj.leagueUnitId)),
        ("league_unit_name", JsString(obj.leagueUnitName)),
        ("team_id", JsNumber(obj.teamId)),
        ("team_name", JsString(obj.teamName)),
        ("time", DateTimeFormat.write(obj.date)),
        ("round", JsNumber(obj.round)),
        ("match_id", JsNumber(obj.matchId)),
        ("is_home_match", JsNumber(obj.isHomeMatch.id)),
        ("goals", JsNumber(obj.goals)),
        ("enemy_goals", JsNumber(obj.enemyGoals)),
        ("sold_total", JsNumber(obj.soldTotal)),
        ("formation", JsString(obj.formation)),
        ("tactic_type", JsNumber(obj.tacticType)),
        ("tactic_skill", JsNumber(obj.tacticSkill)),
        ("rating_midfield", JsNumber(obj.ratingMidfield)),
        ("rating_right_def", JsNumber(obj.ratingRightDef)),
        ("rating_left_def", JsNumber(obj.ratingLeftDef)),
        ("rating_mid_def", JsNumber(obj.ratingMidDef)),
        ("rating_right_att", JsNumber(obj.ratingRightAtt)),
        ("rating_mid_att", JsNumber(obj.ratingMidAtt)),
        ("rating_left_att", JsNumber(obj.ratingLeftAtt)),
        ("rating_indirect_set_pieces_def", JsNumber(obj.ratingIndirectSetPiecesDef)),
        ("rating_indirect_set_pieces_att", JsNumber(obj.ratingIndirectSetPiecesAtt)),
        ("opposite_team_id", JsNumber(obj.oppositeTeamId)),
        ("opposite_team_name", JsString(obj.oppositeTeamName)),
        ("opposite_formation", JsString(obj.oppositeFormation)),
        ("opposite_tactic_type", JsNumber(obj.oppositeTacticType)),
        ("opposite_tactic_skill", JsNumber(obj.oppositeTacticSkill)),
        ("opposite_rating_midfield", JsNumber(obj.oppositeRatingMidfield)),
        ("opposite_rating_right_def", JsNumber(obj.oppositeRatingRightDef)),
        ("opposite_rating_left_def", JsNumber(obj.oppositeRatingLeftDef)),
        ("opposite_rating_mid_def", JsNumber(obj.oppositeRatingMidDef)),
        ("opposite_rating_right_att", JsNumber(obj.oppositeRatingRightAtt)),
        ("opposite_rating_mid_att", JsNumber(obj.oppositeRatingMidAtt)),
        ("opposite_rating_left_att", JsNumber(obj.oppositeRatingLeftAtt)),
        ("opposite_rating_indirect_set_pieces_def", JsNumber(obj.oppositeRatingIndirectSetPiecesDef)),
        ("opposite_rating_indirect_set_pieces_att", JsNumber(obj.oppositeRatingIndirectSetPiecesAtt)),
      )
    }
  }

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
      enemyGoals = oppositeTeam.goals,

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

      oppositeTeamId = if(oppositeTeam.teamId < 0) 0 else oppositeTeam.teamId,
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
