package models.web

import databases.clickhouse._
import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import service.DefaultService

case class ViewData[T, +V <: AbstractWebDetails](details: V,
                                                webPagedEntities: WebPagedEntities[T],
                                                links: Links)



@Singleton
class ViewDataFactory @Inject() (val defaultService: DefaultService) {

  def create[T, V <: AbstractWebDetails](details: V,
                                         func: StatisticsParameters => Call,
                                         statisticsType: StatisticsType,
                                         statisticsParameters: StatisticsParameters,
                                         statisticsCHRequest: StatisticsCHRequest[T],
                                         entities: List[T]): ViewData[T, V] = {
    val seasonLinks = SeasonLinks(statisticsParameters.season,
      defaultService.seasonsWithLinks(details.league.getLeagueId, seasonInfoUrlFunc(statisticsParameters, func)))

    val currentRound = defaultService.currentRound(details.league.getLeagueId)
    val statsTypeFunc = statTypeUrlFunc(statisticsParameters, func)
    val statTypeLinks = statisticsType match {
      case AvgMax => StatTypeLinks.withAverages(statsTypeFunc, currentRound, statisticsParameters.statsType)
      case Accumulated => StatTypeLinks.withAccumulator(statsTypeFunc, currentRound, statisticsParameters.statsType)
      case OnlyRound => StatTypeLinks.onlyRounds(statsTypeFunc, currentRound, statisticsParameters.statsType)
    }

    val sortByLinks = SortByLinks(sortByUrlFunc(statisticsParameters, func), statisticsCHRequest.sortingColumns, statisticsParameters.sortBy)

    val pageSizeLinks = PageSizeLinks(pageSizeUrlFunc(statisticsParameters, func), statisticsParameters.pageSize)

    val sortingDirectionLinks = SortingDirectionLinks(sortingDirectionUrlFunc(statisticsParameters, func), statisticsParameters.sortingDirection)

    val webPagedEntities = WebPagedEntities(entities, statisticsParameters.page, statisticsParameters.pageSize, pageUrlFunc(statisticsParameters, func))

    val links = Links(seasonLinks = seasonLinks,
      statTypeLinks = statTypeLinks,
      sortByLinks = sortByLinks,
      pageSizeLinks = pageSizeLinks,
      sortingDirectionLinks = sortingDirectionLinks)

    ViewData(details = details,
      links = links,
      webPagedEntities = webPagedEntities)
  }

  private def pageUrlFunc(statisticsParameters: StatisticsParameters, func: StatisticsParameters => Call): Int => String = p =>
    func(StatisticsParameters(statisticsParameters.season, p, statisticsParameters.statsType, statisticsParameters.sortBy, statisticsParameters.pageSize, statisticsParameters.sortingDirection)).url

  private def seasonInfoUrlFunc(statisticsParameters: StatisticsParameters, func: StatisticsParameters => Call): Int => String = s =>
    func(StatisticsParameters(s, statisticsParameters.page, statisticsParameters.statsType, statisticsParameters.sortBy, statisticsParameters.pageSize, statisticsParameters.sortingDirection)).url

  private def statTypeUrlFunc(statisticsParameters: StatisticsParameters, func: StatisticsParameters => Call): StatsType => String = st =>
    func(StatisticsParameters(statisticsParameters.season, statisticsParameters.page, st, statisticsParameters.sortBy, statisticsParameters.pageSize, statisticsParameters.sortingDirection)).url

  private def sortByUrlFunc(statisticsParameters: StatisticsParameters, func: StatisticsParameters => Call): String => String = sb =>
    func(StatisticsParameters(statisticsParameters.season, statisticsParameters.page, statisticsParameters.statsType, sb, statisticsParameters.pageSize, statisticsParameters.sortingDirection)).url

  private def pageSizeUrlFunc(statisticsParameters: StatisticsParameters, func: StatisticsParameters => Call): Int => String = ps =>
    func(StatisticsParameters(statisticsParameters.season, 0 /*scroll to beginning*/, statisticsParameters.statsType, statisticsParameters.sortBy, ps, statisticsParameters.sortingDirection)).url

  private def sortingDirectionUrlFunc(statisticsParameters: StatisticsParameters, func: StatisticsParameters => Call): SortingDirection => String = sd =>
    func(StatisticsParameters(statisticsParameters.season, statisticsParameters.page, statisticsParameters.statsType, statisticsParameters.sortBy, statisticsParameters.pageSize, sd)).url
}