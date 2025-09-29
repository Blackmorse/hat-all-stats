package chpp.avatars.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Player(playerId: Long, backgroundUrl: String, layers: Seq[Layer])

case class Layer(image: String, x: Int, y: Int)

object Layer {
  implicit val reader: XmlReader[Layer] = (
    (__ \ "Image").read[String],
    attribute[Int]("x"),
    attribute[Int]("y")
  ).mapN(apply)
}

object Player {
  implicit val reader: XmlReader[Player] = (
    (__ \ "PlayerID").read[Long],
    (__ \ "Avatar" \ "BackgroundImage").read[String],
    (__ \ "Avatar" \ "Layer" ).read(seq[Layer])
  ).mapN(apply)
}
