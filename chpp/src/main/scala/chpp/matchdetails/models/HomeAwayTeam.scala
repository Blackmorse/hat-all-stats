package chpp.matchdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}

case class HomeAwayTeam(teamId: Long,
                        teamName: String,
                        goals: Int,

                        dressUri: String,
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
                       )
abstract class HomeAwayTeamBase {
  def readerBase(idFieldName: String, nameFieldName: String, goalsFieldName: String):
            XmlReader[HomeAwayTeam] = (
    (__ \ idFieldName).read[Long],
    (__ \ nameFieldName).read[String],
    (__ \ goalsFieldName).read[Int],
    (__ \ "DressURI").read[String],
    (__ \ "Formation").read[String],
    (__ \ "TacticType").read[Int],
    (__ \ "TacticSkill").read[Int],
    (__ \ "RatingMidfield").read[Int],
    (__ \ "RatingRightDef").read[Int],
    (__ \ "RatingLeftDef").read[Int],
    (__ \ "RatingMidDef").read[Int],
    (__ \ "RatingRightAtt").read[Int],
    (__ \ "RatingMidAtt").read[Int],
    (__ \ "RatingLeftAtt").read[Int],
    (__ \ "RatingIndirectSetPiecesDef").read[Int],
    (__ \ "RatingIndirectSetPiecesAtt").read[Int],
    ).mapN(HomeAwayTeam.apply)
}

object HomeTeam extends HomeAwayTeamBase {
  implicit val reader: XmlReader[HomeAwayTeam] = readerBase("HomeTeamID", "HomeTeamName", "HomeGoals")
}

object AwayTeam extends HomeAwayTeamBase {
  implicit val reader: XmlReader[HomeAwayTeam] = readerBase("AwayTeamID", "AwayTeamName", "AwayGoals")
}