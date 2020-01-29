package models.web

abstract class AbstractWebDetails {
  val leagueId: Int
  val seasonInfo: SeasonInfo
  val statTypeLinks: StatTypeLinks
  val sortByLinks: SortByLinks
}

case class SeasonInfo(season: Int, allSeasons: Seq[(String, String)])

case class StatTypeLinks(links: Seq[(String, String)], currentStatType: StatsType)

object StatTypeLinks {
  def withAverages(statTypeUrlFunc: StatsType => String, round: Int, current: StatsType): StatTypeLinks = {
    val seq = Seq(Avg.function -> statTypeUrlFunc(Avg), Max.function -> statTypeUrlFunc(Max)) ++
      withoutAverages(statTypeUrlFunc, round, current).links

    StatTypeLinks(seq, current)
  }

  def withoutAverages(statTypeUrlFunc: StatsType => String, round: Int, current: StatsType): StatTypeLinks = {
    StatTypeLinks((1 to round) map (roundNumber => (roundNumber.toString, statTypeUrlFunc(Round(roundNumber)))), current)
  }
}

case class SortByLinks(links: Seq[(String, String)], currentSort: String)

object SortByLinks {
  def apply(sortByFunc: String => String, columns: Seq[String], currentSort: String): SortByLinks = {
    SortByLinks(columns.map(column => column -> sortByFunc(column)), currentSort)
  }
}