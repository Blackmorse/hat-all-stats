package com.blackmorse.hattid.web.models.web.matches

import chpp.matchdetails.models.HomeAwayTeam
import com.blackmorse.hattid.web.databases.requests.model.`match`.MatchRatings
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

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
  implicit val jsonEncoder: JsonEncoder[SingleMatch] = DeriveJsonEncoder.gen[SingleMatch]
  implicit val jsonDecoder: JsonDecoder[SingleMatch] = DeriveJsonDecoder.gen[SingleMatch]

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
