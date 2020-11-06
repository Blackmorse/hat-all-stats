package databases.requests.overview.model

import anorm.SqlParser.get
import anorm.~
import play.api.libs.json.Json

case class FormationsOverview(formation: String, count: Int)

object FormationsOverview {
  implicit val writes = Json.writes[FormationsOverview]

  val mapper = {
    get[String]("formation") ~
    get[Int]("count") map {
      case formation ~ count => FormationsOverview(formation, count)
    }
  }
}
