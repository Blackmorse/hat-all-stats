package models.web.translations

import play.api.libs.json.{Json, OWrites}

case class TranslationLevel(level: Int, translation: String)

case class LanguageTranslations(skillTranslations: Seq[TranslationLevel],
                                specialities: Seq[TranslationLevel])

object TranslationLevel {
  implicit val writes: OWrites[TranslationLevel] = Json.writes[TranslationLevel]
}

object LanguageTranslations {
  implicit val writes: OWrites[LanguageTranslations] = Json.writes[LanguageTranslations]
}