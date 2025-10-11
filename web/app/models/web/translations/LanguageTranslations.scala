package models.web.translations

import chpp.translations.models.Translations
import play.api.libs.json.{Json, OWrites}

case class TranslationLevel(level: Int, translation: String)

case class LanguageTranslations(skillTranslations: Seq[TranslationLevel],
                                specialities: Seq[TranslationLevel])

object TranslationLevel {
  implicit val writes: OWrites[TranslationLevel] = Json.writes[TranslationLevel]
}

object LanguageTranslations {
  implicit val writes: OWrites[LanguageTranslations] = Json.writes[LanguageTranslations]

  def apply(translations: Translations) = new LanguageTranslations(
    skillTranslations = translations.skillLevels.map(sl => TranslationLevel(sl.level, sl.name)),
    specialities = translations.specialities.map(sl => TranslationLevel(sl.level, sl.name))
  )
}