package models.web.matches

import chpp.matchdetails.models.HomeAwayTeam
import databases.requests.model.`match`.MatchRatings
import play.api.libs.json.{Json, OWrites, Reads}

case class SingleMatch(homeTeamName: String,
                       homeTeamId: Long,
                       homeGoals: Option[Int],
                       awayTeamName: String,
                       awayTeamId: Long,
                       awayGoals: Option[Int],
                       matchId: Option[Long],
                       homeMatchRatings: MatchRatings,
                       awayMatchRatings: MatchRatings)

object SingleMatch {
  implicit val writes: OWrites[SingleMatch] = Json.writes[SingleMatch]
  implicit val reads: Reads[SingleMatch] = Json.reads[SingleMatch]

  def fromHomeAwayTeams(homeTeam: HomeAwayTeam,
                        awayTeam: HomeAwayTeam,
                        homeGoals: Option[Int],
                        awayGoals: Option[Int],
                        matchId: Option[Long]): SingleMatch = {
    SingleMatch(homeTeamName = homeTeam.teamName,
      homeTeamId = homeTeam.teamId,
      homeGoals = homeGoals,
      awayTeamName = awayTeam.teamName,
      awayTeamId = awayTeam.teamId,
      awayGoals = awayGoals,
      matchId = matchId,
      homeMatchRatings = MatchRatings(
        formation = homeTeam.formation,
        tacticType = homeTeam.tacticType,
        tacticSkill = homeTeam.tacticSkill,
        ratingMidfield = homeTeam.ratingMidfield,
        ratingRightDef = homeTeam.ratingRightDef,
        ratingMidDef = homeTeam.ratingMidDef,
        ratingLeftDef = homeTeam.ratingLeftDef,
        ratingRightAtt = homeTeam.ratingRightAtt,
        ratingMidAtt = homeTeam.ratingMidAtt,
        ratingLeftAtt = homeTeam.ratingLeftAtt,
        ratingIndirectSetPiecesDef = homeTeam.ratingIndirectSetPiecesDef,
        ratingIndirectSetPiecesAtt = homeTeam.ratingIndirectSetPiecesAtt
      ),
      awayMatchRatings = MatchRatings(
        formation = awayTeam.formation,
        tacticType = awayTeam.tacticType,
        tacticSkill = awayTeam.tacticSkill,
        ratingMidfield = awayTeam.ratingMidfield,
        ratingRightDef = awayTeam.ratingRightDef,
        ratingMidDef = awayTeam.ratingMidDef,
        ratingLeftDef = awayTeam.ratingLeftDef,
        ratingRightAtt = awayTeam.ratingRightAtt,
        ratingMidAtt = awayTeam.ratingMidAtt,
        ratingLeftAtt = awayTeam.ratingLeftAtt,
        ratingIndirectSetPiecesDef = awayTeam.ratingIndirectSetPiecesDef,
        ratingIndirectSetPiecesAtt = awayTeam.ratingIndirectSetPiecesAtt
      ))
  }
}
