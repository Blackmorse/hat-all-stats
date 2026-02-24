package databases.requests.model.team

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import databases.requests.model.Chart
import play.api.libs.json.{Json, OWrites}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class TeamPowerRatingChart(teamSortingKey: TeamSortingKey,
                                season: Int,
                                round: Int,
                                powerRating: Int) extends Chart

object TeamPowerRatingChart {
  implicit val writes: OWrites[TeamPowerRatingChart] = Json.writes[TeamPowerRatingChart]
  implicit val jsonEncoder: JsonEncoder[TeamPowerRatingChart] = DeriveJsonEncoder.gen[TeamPowerRatingChart]
  
  val mapper: RowParser[TeamPowerRatingChart] = {
    get[Int]("league_id") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("season") ~
      get[Int]("round") ~
      get[Int]("power_rating") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ season ~ round ~ powerRating =>
        val teamSortingKey = TeamSortingKey(teamId = teamId,
          teamName = teamName,
          leagueUnitId = leagueUnitId,
          leagueUnitName = leagueUnitName,
          leagueId = leagueId)
        TeamPowerRatingChart(teamSortingKey = teamSortingKey,
          season = season,
          round = round,
          powerRating = powerRating)
    }
  }
}
