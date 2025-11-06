package databases.requests.model.team

import play.api.libs.json.{Json, OWrites}
import anorm.SqlParser.get
import anorm.{RowParser, ~}
import databases.requests.model.Chart
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class TeamRatingChart(teamSortingKey: TeamSortingKey,
                           season: Int,
                           round: Int,
                           rating: Int,
                           ratingEndOfMatch: Int) extends Chart

object TeamRatingChart {
  implicit val writes: OWrites[TeamRatingChart] = Json.writes[TeamRatingChart]
  implicit val jsonEncoder: JsonEncoder[TeamRatingChart] = DeriveJsonEncoder.gen[TeamRatingChart]

  val mapper: RowParser[TeamRatingChart] = {
    get[Int]("league") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("season") ~
      get[Int]("round") ~
      get[Int]("rating") ~
      get[Int]("rating_end_of_match") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
        season ~ round ~ rating ~ ratingEndOfMatch =>
        val teamSortingKey = TeamSortingKey(teamId = teamId,
          teamName = teamName,
          leagueUnitId = leagueUnitId,
          leagueUnitName = leagueUnitName,
          leagueId = leagueId)
        TeamRatingChart(teamSortingKey = teamSortingKey,
          season = season,
          round = round,
          rating = rating,
          ratingEndOfMatch = ratingEndOfMatch)
    }
  }
}
