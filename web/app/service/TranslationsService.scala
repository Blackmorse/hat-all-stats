package service

import chpp.translations.TranslationsRequest
import chpp.translations.models.Translations
import models.web.translations.{LanguageTranslations, TranslationLevel}
import webclients.ChppClient

import scala.concurrent.duration._
import javax.inject.{Inject, Singleton}
import scala.concurrent.Await

@Singleton
class TranslationsService @Inject() (val chppClient: ChppClient) {
  private val languages = Set(
    "en" -> 2,   //English
    "de" -> 3,   //German
    "it" -> 4,   //Italian
    "tr" -> 19,  //Turkish
    "ru" -> 14,  //Russian
    "es" -> 6,   //Spanish
    "hr" -> 39,  //Croatian
    "fa" -> 75   //Persian
  )

  val translationsMap: Seq[(String, LanguageTranslations)] = {
    languages.map {case (languageAbbr, languageId) =>
      languageAbbr -> chppClient.executeUnsafe[Translations, TranslationsRequest](TranslationsRequest(languageId = languageId))
    }.map {case (languageAbbr, translationsFuture) =>
      val translations = Await.result(translationsFuture, 60.seconds)
      languageAbbr -> LanguageTranslations(
        skillTranslations = translations.skillLevels.map(sl => TranslationLevel(sl.level, sl.name)),
        specialities = translations.specialities.map(sl => TranslationLevel(sl.level, sl.name))
      )
    }.toSeq
  }
}
