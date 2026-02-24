package databases.requests.model.team

import play.api.libs.json.{Json, OWrites}
import anorm.SqlParser.get
import anorm.{RowParser, ~}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class TeamRating(teamSortingKey: TeamSortingKey,
                      rating: Int,
                      ratingEndOfMatch: Int)

object TeamRating {
  implicit val writes: OWrites[TeamRating] = Json.writes[TeamRating]
  implicit val jsonEncoder: JsonEncoder[TeamRating] = DeriveJsonEncoder.gen[TeamRating]

  val mapper: RowParser[TeamRating] = {
      get[Int]("league") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("rating") ~
      get[Int]("rating_end_of_match") map {
        case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
          rating ~ ratingEndOfMatch =>
          val teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId)
          TeamRating(teamSortingKey, rating, ratingEndOfMatch)
    }
  }
}
