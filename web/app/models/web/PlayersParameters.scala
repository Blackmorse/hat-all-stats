package models.web

import play.api.mvc.QueryStringBindable

case class PlayersParameters(role: Option[String], nationality: Option[Int], minAge: Option[Int], maxAge: Option[Int])

object PlayersParameters {
  implicit def queryStringBindable(implicit stringBuilder: QueryStringBindable[String]): QueryStringBindable[PlayersParameters] = new QueryStringBindable[PlayersParameters] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, PlayersParameters]] = {
      val roleOptionEither = stringBuilder.bind("role", params)
        .map(roleEither => roleEither.map(role => if (role.isEmpty) None else Some(role)))
        .orElse(Some(Right(None)))

      val nationalityOptionEither = ParametersUtils.bindInt("nationality", params)
        .map(nationalityEither => nationalityEither.map(nationality => Some(nationality)))
        .orElse(Some(Right(None)))

      val minAgeOptionEither = ParametersUtils.bindInt("minAge", params)
        .map(minAgeEither => minAgeEither.map(minAge => Some(minAge)))
        .orElse(Some(Right(None)))

      val maxAgeOptionEither = ParametersUtils.bindInt("maxAge", params)
        .map(maxAgeEither => maxAgeEither.map(maxAge => Some(maxAge)))
        .orElse(Some(Right(None)))

      for (roleEither <- roleOptionEither;
           nationalityEither <- nationalityOptionEither;
           minAgeEither <- minAgeOptionEither;
           maxAgeEither <- maxAgeOptionEither) yield {
        for(role <- roleEither;
            nationality <- nationalityEither;
            minAge <- minAgeEither;
            maxAge <- maxAgeEither) yield PlayersParameters(role, nationality, minAge, maxAge)
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
