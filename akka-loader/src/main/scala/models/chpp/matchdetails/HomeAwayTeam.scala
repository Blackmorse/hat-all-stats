package models.chpp.matchdetails

import com.lucidchart.open.xtract.{XmlReader, __}
import cats.syntax.all._

case class HomeAwayTeam(teamId: Int,
                        teamName: String,
                        goals: Int,

                         dressUri: String,
                        formation: String,
                        tacticType: Int,
                        tacticSkill: Int,
                        ratingMidfied: Int,
                        ratingRightDef: Int,
                        ratingLeftDef: Int,
                        ratingMidDef: Int,
                        ratingRightAtt: Int,
                        ratingMidAtt: Int,
                        ratingLeftAtt: Int,
                        ratingIndirectSetPiecesDef: Int,
                        ratingInderectSetPiecesAtt: Int,
                       )
abstract class HomeAwayTeamBase {
  def readerBase(idFieldName: String, nameFieldName: String, goalsFieldName: String):
            XmlReader[HomeAwayTeam] = (
    (__ \ idFieldName).read[Int],
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