package com.blackmorse.hattid.web.service

import com.blackmorse.hattid.web.models.web.HattidError
import com.blackmorse.hattid.web.models.web.translations.LanguageTranslations
import com.blackmorse.hattid.web.zios.CHPPServices
import zio.http.Client
import zio.{ZIO, ZLayer}

object TranslationsService {
  private val languages = Seq(
    "en" -> 2, //English
    "de" -> 3, //German
    "it" -> 4, //Italian
    "tr" -> 19, //Turkish
    "ru" -> 14, //Russian
    "es" -> 6, //Spanish
    "hr" -> 39, //Croatian
    "fa" -> 75 //Persian
  )
  
  lazy val layer: ZLayer[CHPPServices, HattidError, TranslationsService] = ZLayer {
    for {
      chppService  <- ZIO.service[ChppService]
      translations <- ZIO.foreach(languages.map { case (languageAbbr, languageId) =>
        languageAbbr -> chppService.translations(languageId)
      }) { case (languageAbbr, zio) => zio.map(translations => languageAbbr -> LanguageTranslations(translations)) }
    } yield TranslationsService(translations)
  }
}

case class TranslationsService(translations: Seq[(String, LanguageTranslations)])
