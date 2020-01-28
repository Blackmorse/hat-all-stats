package models.web

import service.DefaultService

case class WebPagedEntities[T](entities: List[T], page: Int, prevUrl: Option[String], nextUrl: Option[String],
                               statTypeLinks: StatTypeLinks)

object WebPagedEntities {
  def apply[T](entities: List[T],
               page: Int,
               pageUrlFunc: Int => String,
               statTypeLinks: StatTypeLinks = StatTypeLinks(Seq(), Avg)): WebPagedEntities[T] = {
    val prevUrl = if(page <= 0) None else Some(pageUrlFunc(page - 1))
    val nextUrl = if (entities.size < DefaultService.PAGE_SIZE) None else Some(pageUrlFunc(page + 1))
    WebPagedEntities(entities, page, prevUrl, nextUrl, statTypeLinks)
  }
}

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
