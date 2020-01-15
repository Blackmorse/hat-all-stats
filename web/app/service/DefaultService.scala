package service

import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import play.api.Configuration

import collection.JavaConverters._

@Singleton
class DefaultService @Inject() (val hattrick: Hattrick,
                                val configuration: Configuration) {
  lazy val leagueIdToCountryNameMap = hattrick.api.worldDetails().execute()
    .getLeagueList.asScala.map(league => league.getLeagueId -> league.getEnglishName)
    .toMap

  lazy val currentSeason = configuration.get[Int]("hattrick.currentSeason")

  lazy val defaultLeagueId = configuration.get[Int]("hattrick.defaultLeagueId")
}
