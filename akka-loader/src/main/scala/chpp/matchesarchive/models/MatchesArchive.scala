package chpp.matchesarchive.models

import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}

case class MatchesArchive(isYouth: Boolean,
                          team: Team)

object MatchesArchive {
  implicit val reader: XmlReader[MatchesArchive] = (
    (__ \ "IsYouth").read[Boolean],
    (__ \ "Team").read[Team],
    ).mapN(apply _)
}
