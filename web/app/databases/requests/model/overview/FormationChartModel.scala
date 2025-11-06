package databases.requests.model.overview

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import play.api.libs.json.{Json, OWrites}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class FormationChartModel(formation: String, season: Int, round: Int, count: Int)

object FormationChartModel {
  implicit val writes: OWrites[FormationChartModel] = Json.writes[FormationChartModel]
  implicit val jsonEncoder: JsonEncoder[FormationChartModel] = DeriveJsonEncoder.gen[FormationChartModel]
  
  val mapper: RowParser[FormationChartModel] = {
    get[String]("formation") ~
    get[Int]("season") ~
    get[Int]("round") ~
    get[Int]("count") map {
      case formation ~ season ~ round ~ count =>
        FormationChartModel(
          formation = formation,
          season = season,
          round = round,
          count = count
        )
    }
  }
}