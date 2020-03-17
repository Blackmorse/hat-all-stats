package models.web

case class Links(seasonLinks: SeasonLinks,
                 statTypeLinks: StatTypeLinks,
                 sortByLinks: SortByLinks,
                 pageSizeLinks: PageSizeLinks,
                 sortingDirectionLinks: SortingDirectionLinks)

case class SeasonLinks(season: Int, allSeasons: Seq[(String, String)])

case class StatTypeLinks(links: Seq[(String, String)], currentStatType: StatsType)

case class PageSizeLinks(links: Seq[(String, String)], currentPageSize: Int)

case class SortingDirectionLinks(links: Seq[(String, String)], currentDirection: SortingDirection)

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

case class SortLink(columnName: String, localizationKey: String, link: String)

case class SortByLinks(links: Seq[SortLink], currentSort: String)

object SortByLinks {
  def apply(sortByFunc: String => String, columns: Seq[(String, String)], currentSort: String): SortByLinks = {
    SortByLinks(columns.map{case(column, localizationKey) =>
      SortLink(column, localizationKey, sortByFunc(column))}, currentSort)
  }
}

object PageSizeLinks {
  private val pageSizes = Seq(16, 32, 64)

  def apply(pageSizeFunc: Int => String, currentPageSize: Int): PageSizeLinks = {
    PageSizeLinks(pageSizes.map(ps => (ps.toString, pageSizeFunc(ps))), currentPageSize)
  }
}

object SortingDirectionLinks {
  def apply(sortingDirectionFunc: SortingDirection => String,
            currentSortingDirection: SortingDirection): SortingDirectionLinks = {
    val seq = Seq((Asc.toString, sortingDirectionFunc(Asc)), (Desc.toString, sortingDirectionFunc(Desc)))
    SortingDirectionLinks(seq, currentSortingDirection)
  }
}