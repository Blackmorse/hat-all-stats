package databases.requests.overview.model

import databases.requests.model.`match`.MatchTopHatstats
import play.api.libs.json.Json
import anorm.~
import anorm.SqlParser.get

case class MatchTopHatstatsOverview(leagueId: Int, matchTopHatstats: MatchTopHatstats)

object MatchTopHatstatsOverview {
  implicit val writes = Json.writes[MatchTopHatstatsOverview]

  val mapper = {
    get[Int]("league_id") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("opposite_team_id") ~
    get[String]("opposite_team_name") ~
    get[Long]("match_id") ~
    get[String]("is_home_match") ~
    get[Int]("goals") ~
    get[Int]("enemy_goals") ~
    get[Int]("hatstats") ~
    get[Int]("opposite_hatstats") map {
      case leagueId ~ leagueUnitId ~ leagueUnitName ~ teamId ~ teamName ~ oppositeTeamId
        ~ oppositeTeamName ~ matchId ~ isHomeMatch ~ goals ~ enemyGoals
        ~ hatstats ~ oppositeHatstats =>
        val matchTopHatstats = MatchTopHatstats(leagueUnitId, leagueUnitName, teamId, teamName, oppositeTeamId,
          oppositeTeamName, matchId, isHomeMatch, goals, enemyGoals,
          hatstats, oppositeHatstats)

        MatchTopHatstatsOverview(leagueId, matchTopHatstats)
      }
    }
}
