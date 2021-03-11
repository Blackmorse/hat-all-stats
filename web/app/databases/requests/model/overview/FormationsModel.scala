package databases.requests.model.overview

import anorm.SqlParser.get
import anorm.~

case class FormationsModel(formation: String, count: Int)

object FormationsModel {
  val mapper = {
    get[String]("formation") ~
    get[Int]("count") map {
      case formation ~ count => FormationsModel(formation, count)
    }
  }
}
