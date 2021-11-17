package databases.requests.model.overview

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import play.api.libs.json.{Json, OWrites}

case class NumbersChartModel(
                            season: Int,
                            round: Int,
                            count: Int)

object NumbersChartModel {
  implicit val writes: OWrites[NumbersChartModel] = Json.writes[NumbersChartModel]

  val mapper: RowParser[NumbersChartModel] = {
    get[Int]("season") ~
    get[Int]("round") ~
    get[Int]("count") map {
      case season ~ round ~ count =>
        NumbersChartModel(
          season = season,
          round = round,
          count = count
        )
    }
  }
}
