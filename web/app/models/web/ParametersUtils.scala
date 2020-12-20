package models.web

import play.api.mvc.QueryStringBindable

object ParametersUtils {
  def bindInt(name: String, params: Map[String, Seq[String]])(implicit stringBinder: QueryStringBindable[String]): Option[Either[String, Int]] = {
    stringBinder.bind(name, params).map(either => either.flatMap(value => {
      if (value forall Character.isDigit) {
        Right(value.toInt)
      } else {
        Left(s"Error while parsing $name")
      }
    }))
  }
}
