package models.web

import play.api.mvc.QueryStringBindable

case class RestStatisticsParameters(page: Int,
                                    pageSize: Int,
                                    sortBy: String, sortingDirection: SortingDirection,
                                    statsType: StatsType,
                                    season: Int)

object RestStatisticsParameters {
  implicit def queryStringBindable(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[RestStatisticsParameters] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, RestStatisticsParameters]] = {
      val pageOptionEither = ParametersUtils.bindInt("page", params)

      val seasonOptionEither = ParametersUtils.bindInt("season", params)

      val pageSizeOptionEither = ParametersUtils.bindInt("pageSize", params)

      val sortByOptionEither = stringBinder.bind("sortBy", params)

      val directionOptionEither = stringBinder.bind("sortDirection", params)
        .map(directionEither => directionEither.flatMap(direction => {
          if (direction == "asc") {
            Right(Asc)
          } else if (direction == "desc") {
            Right(Desc)
          } else {
            Left("Unknown sorting direction")
          }
        }))

      val statsTypeOptionEither = StatsType.queryStringBindable.bind("", params)

      for(pageSizeEither <- pageSizeOptionEither;
          pageEither <- pageOptionEither;
          sortByEither <- sortByOptionEither;
          directionEither <- directionOptionEither;
          statsTypeEither <- statsTypeOptionEither;
          seasonEither <- seasonOptionEither) yield {
        for(pageSize <- pageSizeEither;
            page <- pageEither;
            sortBy <- sortByEither;
            direction <- directionEither;
            statsType <- statsTypeEither;
            season <- seasonEither) yield {
          RestStatisticsParameters(page, pageSize, sortBy, direction, statsType, season)
        }
      }
    }

    override def unbind(key: String, value: RestStatisticsParameters): String = {
      val statsTypeStr = StatsType.queryStringBindable.unbind("", value.statsType)

      stringBinder.unbind("page", value.page.toString) + "&" +
        stringBinder.unbind("pageSize", value.pageSize.toString) + "&" +
        stringBinder.unbind("sortBy", value.sortBy) + "&" +
        stringBinder.unbind("sortDirection",  if (value.sortingDirection == Asc)  "asc" else "desc" ) + "&" +
        statsTypeStr
    }
  }
}
