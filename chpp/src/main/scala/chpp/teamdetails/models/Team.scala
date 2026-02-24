package chpp.teamdetails.models

import java.util.Date
import cats.syntax.all._
import chpp.BaseXmlMapper
import chpp.commonmodels.{League, PowerRating}
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Team(teamId: Int,
                teamName: String,
                shortTeamName: String,
                isPrimaryClub: Boolean,
                foundedDate: Date,
                arena: Arena,
                league: League,
                country: Country,
                region: Region,
                trainer: Trainer,
                homePage: String,
                powerRating: PowerRating,
                friendlyTeamId: Option[Int],
                leagueLevelUnit: LeagueLevelUnit,
                numberOfVictories: Option[Int],
                numberOfUndefeated: Option[Int],
                fanclub: Fanclub,
                youthTeamId: Int,
                youthTeamName: String,
                flags: Flags,
                trophyList: Seq[Trophy])

object Team extends BaseXmlMapper {
  implicit val reader: XmlReader[Team] = (
    (__ \ "TeamID").read[Int],
    (__ \ "TeamName").read[String],
    (__ \ "ShortTeamName").read[String],
    (__ \ "IsPrimaryClub").read[Boolean],
    (__ \ "FoundedDate").read[String].map(date),
    (__ \ "Arena").read[Arena],
    (__ \ "League").read[League],
    (__ \ "Country").read[Country],
    (__ \ "Region").read[Region],
    (__ \ "Trainer").read[Trainer],
    (__ \ "HomePage").read[String],
    (__ \ "PowerRating").read[PowerRating],
    (__ \ "FriendlyTeamID").read[Int].optional,
    (__ \ "LeagueLevelUnit").read[LeagueLevelUnit],
    (__ \ "NumberOfVictories").read[Int].optional,
    (__ \ "NumberOfUndefeated").read[Int].optional,
    (__ \ "Fanclub").read[Fanclub],
    (__ \ "YouthTeamID").read[Int],
    (__ \ "YouthTeamName").read[String],
    (__ \ "Flags").read[Flags],
    (__ \ "TrophyList" \ "Trophy").read(using seq[Trophy]),
    ).mapN(apply)
}