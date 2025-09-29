package chpp.matches.models

import chpp.commonmodels.League
import chpp.teamdetails.models.LeagueLevelUnit
import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Team(teamId: Long,
                teamName: String,
                shortTeamName: String,
                league: League,
                leagueLevelUnit: LeagueLevelUnit,
                matchList: Seq[Match])

object Team {
  implicit val reader: XmlReader[Team] = (
    (__ \ "TeamID").read[Long],
    (__ \ "TeamName").read[String],
    (__ \ "ShortTeamName").read[String],
    (__ \ "League").read[League],
    (__ \ "LeagueLevelUnit").read[LeagueLevelUnit],
    (__ \ "MatchList" \ "Match").read(seq[Match])
  ).mapN(apply)
}
