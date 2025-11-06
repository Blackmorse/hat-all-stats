package databases.requests.model.`match`

import anorm.{RowParser, ~}
import anorm.SqlParser.get
import play.api.libs.json.{Json, OWrites}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class SimilarMatchesStats(wins: Int,
                               draws: Int,
                               loses: Int,
                               avgGoalsFor: Double,
                               avgGoalsAgainst: Double,
                               count: Int)

object SimilarMatchesStats {
  implicit val writes: OWrites[SimilarMatchesStats] = Json.writes[SimilarMatchesStats]
  implicit val jsonEncoder: JsonEncoder[SimilarMatchesStats] = DeriveJsonEncoder.gen[SimilarMatchesStats]
  
  val mapper: RowParser[SimilarMatchesStats] = {
    get[Int]("wins") ~
    get[Int]("draws") ~
    get[Int]("loses") ~
    get[Double]("avg_goals_for") ~
    get[Double]("avg_goals_against") ~
    get[Int]("count") map {
      case wins ~ draws ~ loses ~ avgGoalsFor ~ avgGoalsAgainst ~ count =>
        SimilarMatchesStats(wins = wins,
          draws = draws,
          loses = loses,
          avgGoalsFor = avgGoalsFor,
          avgGoalsAgainst = avgGoalsAgainst,
          count = count)
    }
  }
}
