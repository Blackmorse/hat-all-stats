package models.web

case class Links(seasonLinks: SeasonLinks, statTypeLinks: StatTypeLinks, sortByLinks: SortByLinks)

case class SeasonLinks(season: Int, allSeasons: Seq[(String, String)])

case class StatTypeLinks(links: Seq[(String, String)], currentStatType: StatsType)

object StatTypeLinks {
  def withAverages(statTypeUrlFunc: StatsType => String, round: Int, current: StatsType): StatTypeLinks = {
    val seq = Seq(Avg.function -> statTypeUrlFunc(Avg), Max.function -> statTypeUrlFunc(Max)) ++
      (1 to round).map(roundNumber => (roundNumber.toString, statTypeUrlFunc(Round(roundNumber))))

    StatTypeLinks(seq, current)
  }

  def withAccumulator(statTypeUrlFunc: StatsType => String, round: Int, current: StatsType): StatTypeLinks = {
    val seq = (1 to round).map(roundNumber => (roundNumber.toString, statTypeUrlFunc(Round(roundNumber)))) ++ Seq(("all", statTypeUrlFunc(Accumulate)))
    StatTypeLinks(seq, current)
  }

  def onlyRounds(statTypeUrlFunc: StatsType => String, round: Int, current: StatsType): StatTypeLinks = {
    val seq = (1 to round).map(roundNumber => (roundNumber.toString, statTypeUrlFunc(Round(roundNumber))))
    StatTypeLinks(seq, current)
  }
}

case class SortByLinks(links: Seq[(String, String)], currentSort: String)

object SortByLinks {
  def apply(sortByFunc: String => String, columns: Seq[String], currentSort: String): SortByLinks = {
    SortByLinks(columns.map(column => column -> sortByFunc(column)), currentSort)
  }
}