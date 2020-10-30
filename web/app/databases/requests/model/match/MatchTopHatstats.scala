package databases.requests.model.`match`

import anorm.~
import anorm.SqlParser.get
import databases.requests.model.team.TeamSortingKey
import play.api.libs.json.Json

case class MatchTopHatstats(
                             homeTeam: TeamSortingKey,
                             awayTeam: TeamSortingKey,
                             homeHatstats: Int,
                             homeGoals: Int,
                             awayHatstats: Int,
                             awayGoals: Int,
                             matchId: Long)

object MatchTopHatstats {
  implicit val writes = Json.writes[MatchTopHatstats]

  val mapper = {
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
      case leagueUnitId ~ leagueUnitName ~ teamId ~ teamName ~ oppositeTeamId
        ~ oppositeTeamName ~ matchId ~ isHomeMatch ~ goals ~ enemyGoals
        ~ hatstats ~ oppositeHatstats =>

        val (homeTeam, awayTeam, homeHatstats, awayHatstats, homeGoals, awayGoals) =
          if (isHomeMatch == "home") {
            (TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName),
              TeamSortingKey(oppositeTeamId, oppositeTeamName, leagueUnitId, leagueUnitName),
              hatstats, oppositeHatstats,
              goals, enemyGoals)
          } else {
            (TeamSortingKey(oppositeTeamId, oppositeTeamName, leagueUnitId, leagueUnitName),
              TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName),
              oppositeHatstats, hatstats,
              enemyGoals, goals)
          }

        MatchTopHatstats(homeTeam, awayTeam, homeHatstats, homeGoals, awayHatstats, awayGoals, matchId)
    }
  }
}
