package models.chpp.players

import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}
import com.lucidchart.open.xtract.XmlReader._

case class TrainerData(trainerType: Int,
                       trainerSkill: Int)

object TrainerData {
  implicit val reader: XmlReader[TrainerData] = (
    (__ \ "TrainerType").read[Int],
    (__ \ "TrainerSkill").read[Int],
    ).mapN(apply _)
}
