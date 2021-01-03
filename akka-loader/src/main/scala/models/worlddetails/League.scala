package models.worlddetails

import java.util.Date

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}
import models.BaseXmlMapper

case class League(leagueId: Int,
                  leagueName: String,
                  season: Int,
                  seasonOffset: Int,
                  matchRound: Int,
                  shortName: String,
                  continent: String,
                  englishName: String,
                  country: Option[Country],
                  cups: Seq[Cup],
                  nationalTeamId: Int,
                  u20TeamId: Int,
                  activeTeams: Int,
                  activeUsers: Int,
                  waitingUsers: Int,
                  trainingDate: Date,
                  economyDate: Date,
                  cupMatchDate: Date,
                  seriesMatchDate: Date,
                  numberOfLevels: Int
                 )

object League extends BaseXmlMapper {
  implicit val reader: XmlReader[League] = (
    (__ \ "LeagueID").read[Int],
    (__ \ "LeagueName").read[String],
    (__ \ "Season").read[Int],
    (__ \ "SeasonOffset").read[Int],
    (__ \ "MatchRound").read[Int],
    (__ \ "ShortName").read[String],
    (__ \ "Continent").read[String],
    (__ \ "EnglishName").read[String],
    (__ \ "Country").read[Country].optional,
    (__ \ "Cups" \ "Cup").read(seq[Cup]),
    (__ \ "NationalTeamId").read[Int],
    (__ \ "U20TeamId").read[Int],
    (__ \ "ActiveTeams").read[Int],
    (__ \ "ActiveUsers").read[Int],
    (__ \ "WaitingUsers").read[Int],
    (__ \ "TrainingDate").read[String].map(date),
    (__ \ "EconomyDate").read[String].map(date),
    (__ \ "CupMatchDate").read[String].map(date),
    (__ \ "SeriesMatchDate").read[String].map(date),
    (__ \ "NumberOfLevels").read[Int]
    ).mapN(apply _)
}
