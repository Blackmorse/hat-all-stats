package chpp.players.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class TrainerData(trainerType: Int,
                       trainerSkill: Int)

object TrainerData {
  implicit val reader: XmlReader[TrainerData] = (
    (__ \ "TrainerType").read[Int],
    (__ \ "TrainerSkill").read[Int],
    ).mapN(apply _)
}
