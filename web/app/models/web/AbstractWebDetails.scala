package models.web

abstract class AbstractWebDetails {
  val leagueId: Int
  val seasonInfo: SeasonInfo
}

case class SeasonInfo(season: Int, allSeasons: Seq[(String, String)])
