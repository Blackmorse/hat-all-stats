package chpp.managercompendium.models

import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Team(teamId: Long, 
                teamName: String, 
                genderId: Int, 
                leagueSystemId: Int,
                arena: Arena,
                league: League,
                country: Country,
                leagueLevelUnit: LeagueLevelUnit,
                region: Region,
                youthTeam: Option[YouthTeam]
               )

object Team {
  implicit val reader: XmlReader[Team] = (
    (__ \ "TeamId").read[Long],
    (__ \ "TeamName").read[String],
    (__ \ "GenderID").read[Int],
    (__ \ "LeagueSystemID").read[Int],
    (__ \ "Arena").read[Arena],
    (__ \ "League").read[League],
    (__ \ "Country").read[Country],
    (__ \ "LeagueLevelUnit").read[LeagueLevelUnit],
    (__ \ "Region").read[Region],
    (__ \ "YouthTeam").read[YouthTeam].optional
  ).mapN(apply)
}