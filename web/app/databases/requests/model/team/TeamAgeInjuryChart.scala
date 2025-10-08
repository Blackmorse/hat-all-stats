package databases.requests.model.team

import play.api.libs.json.{Json, OWrites}
import anorm.SqlParser.get
import anorm.{RowParser, ~}
import databases.requests.model.Chart

case class TeamAgeInjuryChart(teamSortingKey: TeamSortingKey,
                              season: Int, 
                              round: Int,
                              age: Int,
                              injury: Int,
                              injuryCount: Int) extends Chart

object TeamAgeInjuryChart {
  implicit val writes: OWrites[TeamAgeInjuryChart] = Json.writes[TeamAgeInjuryChart]

  val mapper: RowParser[TeamAgeInjuryChart] = {
    get[Int]("league") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("season") ~
      get[Int]("round") ~
      get[Int]("age") ~
      get[Int]("injury") ~
      get[Int]("injury_count") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
        season ~ round ~ age ~ injury ~ injuryCount =>
        val teamSortingKey = TeamSortingKey(teamId = teamId,
          teamName = teamName,
          leagueUnitId = leagueUnitId,
          leagueUnitName = leagueUnitName,
          leagueId = leagueId)
        TeamAgeInjuryChart(teamSortingKey = teamSortingKey,
          season = season,
          round = round,
          age = age,
          injury = injury,
          injuryCount = injuryCount)
    }
  }
}
