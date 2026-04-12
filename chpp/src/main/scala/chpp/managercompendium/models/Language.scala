package chpp.managercompendium.models

import com.lucidchart.open.xtract.__
import cats.syntax.all._

case class Language(languageId: Int, languageName: String)

object Language {
  implicit val reader: com.lucidchart.open.xtract.XmlReader[Language] = (
    (__ \ "LanguageId").read[Int],
    (__ \ "LanguageName").read[String],
  ).mapN(apply)
}
