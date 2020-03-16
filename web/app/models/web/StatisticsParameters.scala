package models.web

import play.api.mvc.QueryStringBindable

case class StatisticsParameters(season: Int,
                                page: Int,
                                statsType: StatsType,
                                sortBy: String,
                                pageSize: Int,
                                sortingDirection: SortingDirection)

object StatisticsParameters {
  implicit def queryStringBindable(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[StatisticsParameters] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, StatisticsParameters]] = {
      val seasonRes = stringBinder.bind("season", params).map(a => a.map(_.toInt))
      val pageRes = stringBinder.bind("page", params).map(a => a.map(_.toInt))
      val pageSizeRes = stringBinder.bind("pageSize", params).map(a => a.map(_.toInt))
      val statsTypeRes = stringBinder.bind("statType", params)
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

      val sortByRes = stringBinder.bind("sortBy", params)

      val sortingDirectionRes = stringBinder.bind("sortDirection", params).map(eith => eith.map {
        case "desc" => Desc
        case "asc" => Asc
      })

      for (season <- seasonRes;
           page <- pageRes;
           statsType <- statsTypeRes;
           sortBy <- sortByRes;
           pageSize <- pageSizeRes;
           sortingDirection <- sortingDirectionRes) yield {
        for(s <- season; p <- page; st <- statsType; sb <- sortBy; ps <- pageSize; sd <- sortingDirection) yield StatisticsParameters(s, p, st, sb, ps, sd)
      }
    }

    override def unbind(key: String, value: StatisticsParameters): String = {
      val statsTypeStr = value.statsType match {
        case Avg => stringBinder.unbind("statType", "avg")
        case Max => stringBinder.unbind("statType", "max")
        case Accumulate => stringBinder.unbind("statType", "accumulate")
        case Round(num) => stringBinder.unbind("statType", "statRound") + "&" + stringBinder.unbind("statRoundNumber", num.toString)
      }

      val sortingDirectionStr = value.sortingDirection match {
        case Asc => "asc"
        case Desc => "desc"
      }

      stringBinder.unbind("season", value.season.toString) + "&" +
        stringBinder.unbind("page", value.page.toString) + "&" +
        statsTypeStr + "&" +
        stringBinder.unbind("sortBy", value.sortBy) + "&" +
        stringBinder.unbind("pageSize", value.pageSize.toString) + "&" +
        stringBinder.unbind("sortDirection", sortingDirectionStr)
    }
  }
}