package databases.requests.model.team

import play.api.libs.json.{Json, OWrites}
import anorm.SqlParser.get
import anorm.{RowParser, ~}
import databases.requests.model.Chart

case class TeamStreakTrophiesChart(teamSortingKey: TeamSortingKey,
                                   season: Int,
                                   round: Int,
                                   trophiesNumber: Int,
                                   numberOfVictories: Int,
                                   numberOfUndefeated: Int) extends Chart

object TeamStreakTrophiesChart {
  implicit val writes: OWrites[TeamStreakTrophiesChart] = Json.writes[TeamStreakTrophiesChart]

  val mapper: RowParser[TeamStreakTrophiesChart] = {
    get[Int]("league_id") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("season") ~
      get[Int]("round") ~
      get[Int]("trophies_number") ~
      get[Int]("number_of_victories") ~
      get[Int]("number_of_undefeated") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ season ~ round ~
        trophiesNumber ~ numberOfVictories ~ numberOfUndefeated =>
        val teamSortingKey = TeamSortingKey(teamId = teamId,
          teamName = teamName,
          leagueUnitId = leagueUnitId,
          leagueUnitName = leagueUnitName,
          leagueId = leagueId)

        TeamStreakTrophiesChart(teamSortingKey = teamSortingKey,
          season = season,
          round = round,
          trophiesNumber = trophiesNumber,
          numberOfVictories = numberOfVictories,
          numberOfUndefeated = numberOfUndefeated)
    }
  }
}
