package chpp.managercompendium.models

import chpp.avatars.models.Layer
import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Avatar(backgroundImage: String, layers: Seq[Layer])

object Avatar {
  implicit val reader: com.lucidchart.open.xtract.XmlReader[Avatar] = (
    (__ \ "BackgroundImage").read[String],
    (__ \ "Layer" ).read(using seq[Layer])
  ).mapN(apply)
}
