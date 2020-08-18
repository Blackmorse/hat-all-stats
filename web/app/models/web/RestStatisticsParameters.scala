package models.web

import play.api.mvc.QueryStringBindable

case class RestStatisticsParameters(page: Option[Int])

object RestStatisticsParameters {
  implicit def queryStringBindable(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[RestStatisticsParameters] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, RestStatisticsParameters]] = {
      val r = stringBinder.bind("page", params).map(pageEither => pageEither.flatMap(page => {
        if (page forall Character.isDigit) {
          Right(page.toInt)
        } else {
          Left("Error while parsing page")
        }
      }))

      r.map(rr => rr.map(page => RestStatisticsParameters(Some(page))))
    }

    override def unbind(key: String, value: RestStatisticsParameters): String = {

      value.page.map(pageValue => stringBinder.unbind("page", pageValue.toString)).getOrElse("")
    }
  }
}
