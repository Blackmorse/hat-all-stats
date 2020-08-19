package models.web

import play.api.mvc.QueryStringBindable

case class RestStatisticsParameters(page: Int, pageSize: Int)

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

      val pageSizeOptionEither = stringBinder.bind("pageSize", params).map(pageSizeEither => pageSizeEither.flatMap(pageSize => {
        if (pageSize forall Character.isDigit) {
          Right(pageSize.toInt)
        } else {
          Left("Error while parsing page")
        }
      }))


      for(pageSizeEither <- pageSizeOptionEither;
          pageEither <- pageOptionEither) yield {
        for(pageSize <- pageSizeEither;
            page <- pageEither) yield {
          RestStatisticsParameters(page, pageSize)
        }
      }
    }

    override def unbind(key: String, value: RestStatisticsParameters): String = {
      stringBinder.unbind("page", value.page.toString) + "&" +
        stringBinder.unbind("pageSize", value.pageSize.toString)
    }
  }
}
