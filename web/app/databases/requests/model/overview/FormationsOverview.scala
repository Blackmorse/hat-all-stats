package databases.requests.model.overview

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import play.api.libs.json.{Json, OWrites}

case class FormationsOverview(formation: String, count: Int)

object FormationsOverview {
  implicit val writes: OWrites[FormationsOverview] = Json.writes[FormationsOverview]

  val mapper: RowParser[FormationsOverview] = {
    get[String]("formation") ~
    get[Int]("count") map {
      case formation ~ count => FormationsOverview(formation, count)
    }
  }
}
