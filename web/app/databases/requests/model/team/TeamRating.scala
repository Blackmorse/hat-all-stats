package databases.requests.model.team

import play.api.libs.json.Json
import anorm.SqlParser.get
import anorm.~

case class TeamRating(teamSortingKey: TeamSortingKey,
                      rating: Int,
                      ratingEndOfMatch: Int)

object TeamRating {
  implicit val writes = Json.writes[TeamRating]

  val mapper = {
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
