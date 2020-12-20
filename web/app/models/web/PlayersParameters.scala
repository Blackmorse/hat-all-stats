package models.web

import play.api.mvc.QueryStringBindable

case class PlayersParameters(role: Option[String], nationality: Option[Int])

object PlayersParameters {
  implicit def queryStringBindable(implicit stringBuilder: QueryStringBindable[String]) = new QueryStringBindable[PlayersParameters] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, PlayersParameters]] = {
      val roleOptionEither = stringBuilder.bind("role", params)
        .map(roleEither => roleEither.map(role => Some(role)))
        .orElse(Some(Right(None)))

      val nationalityOptionEither = ParametersUtils.bindInt("nationality", params)
        .map(nationalityEither => nationalityEither.map(nationality => Some(nationality)))
        .orElse(Some(Right(None)))

      for (roleEither <- roleOptionEither;
           nationalityEither <- nationalityOptionEither) yield {
        for(role <- roleEither;
            nationality <- nationalityEither) yield PlayersParameters(role, nationality)
      }
    }

    override def unbind(key: String, value: PlayersParameters): String = {
      List(value.role.map(roleVal => stringBuilder.unbind("role", roleVal)),
        value.nationality.map(nationalityVal => stringBuilder.unbind("nationality", nationalityVal.toString)))
        .flatten
        .mkString("&")
    }
  }
}
