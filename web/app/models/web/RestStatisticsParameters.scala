package models.web

import play.api.mvc.QueryStringBindable

case class RestStatisticsParameters(page: Int, pageSize: Int, sortBy: String, sortingDirection: SortingDirection,
                                    statsType: StatsType, season: Int)

object RestStatisticsParameters {
  implicit def queryStringBindable(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[RestStatisticsParameters] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, RestStatisticsParameters]] = {
      val pageOptionEither = stringBinder.bind("page", params).map(pageEither => pageEither.flatMap(page => {
        if (page forall Character.isDigit) {
          Right(page.toInt)
        } else {
          Left("Error while parsing page")
        }
      }))

      val seasonOptionEither = stringBinder.bind("season", params).map(seasonEither => seasonEither.flatMap(season => {
        if (season forall Character.isDigit) {
          Right(season.toInt)
        } else {
          Left("Error while parsing season")
        }
      }))

      val pageSizeOptionEither = stringBinder.bind("pageSize", params)
        .map(pageSizeEither => pageSizeEither.flatMap(pageSize => {
          if (pageSize forall Character.isDigit) {
            Right(pageSize.toInt)
          } else {
            Left("Error while parsing page")
          }
        }))

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

      val statsTypeOptionEither = stringBinder.bind("statType", params)
        .map(typeEither => typeEither.flatMap{
          case "avg" => Right(Avg)
          case "max" => Right(Max)
          case "accumulate" => Right(Accumulate)
          case "statRound" =>
            stringBinder.bind("statRoundNumber", params)
              .map(statRoundNumberEither => statRoundNumberEither.flatMap(statRoundNumber => {
                if(statRoundNumber forall Character.isDigit)
                  Right(Round(statRoundNumber.toInt): StatsType)
                else
                  Left("Unable to parse")
              })).getOrElse(Left("Unable to parse"))
          case _ => Left("Unable to Parse")
        })

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
      val statsTypeStr = value.statsType match {
        case Avg => stringBinder.unbind("statType", "avg")
        case Max => stringBinder.unbind("statType", "max")
        case Accumulate => stringBinder.unbind("statType", "accumulate")
        case Round(num) => stringBinder.unbind("statType", "statRound") + "&" + stringBinder.unbind("statRoundNumber", num.toString)
      }

      stringBinder.unbind("page", value.page.toString) + "&" +
        stringBinder.unbind("pageSize", value.pageSize.toString) + "&" +
        stringBinder.unbind("sortBy", value.sortBy) + "&" +
        stringBinder.unbind("sortDirection",  if (value.sortingDirection == Asc)  "asc" else "desc" ) + "&" +
        statsTypeStr
    }
  }
}
