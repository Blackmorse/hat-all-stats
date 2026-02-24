package chpp.translations.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader.attribute
import com.lucidchart.open.xtract.{XmlReader, __}
import com.lucidchart.open.xtract.XmlReader._

case class Translations(language: String, skillLevels: Seq[SkillLevel], specialities: Seq[SkillLevel])

case class SkillLevel(level: Int, name: String)

object Translations {
  implicit val reader: XmlReader[Translations] = (
    (__ \ "Language").read[String],
    (__ \ "Texts" \ "SkillLevels" \ "Level").read(using seq[SkillLevel]),
    (__ \ "Texts" \ "PlayerSpecialties" \ "Item").read(using seq[SkillLevel])
  ).mapN(apply)
}

object SkillLevel {
  implicit val reader: XmlReader[SkillLevel] = (
    attribute[Int]("Value"),
    (__).read[String]
  ).mapN(apply)
}
