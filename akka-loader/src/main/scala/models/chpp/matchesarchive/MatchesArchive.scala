package models.chpp.matchesarchive

import com.lucidchart.open.xtract.{XmlReader, __}
import cats.syntax.all._

case class MatchesArchive(isYouth: Boolean,
                          team: Team)

object MatchesArchive {
  implicit val reader: XmlReader[MatchesArchive] = (
    (__ \ "IsYouth").read[Boolean],
    (__ \ "Team").read[Team],
    ).mapN(apply _)
}
