package databases.requests.model.overview

import anorm.SqlParser.get
import anorm.~
import databases.requests.model.team.TeamSortingKey
import play.api.libs.json.Json

case class MatchAttendanceOverview(leagueId: Int,
                                 homeTeams: TeamSortingKey,
                                 awayTeam: TeamSortingKey,
                                   homeGoals: Int,
                                   awayGoals: Int,
                                   matchId: Long,
                                   spectators: Int)

object MatchAttendanceOverview {
  implicit val writes = Json.writes[MatchAttendanceOverview]

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
    get[Int]("spectators")  map {
      case leagueId ~ leagueUnitId ~ leagueUnitName ~ teamId ~ teamName ~
        oppositeTeamId ~ oppositeTeamName ~ matchId ~ isHomeMatch ~
        goals ~ enemyGoals ~ spectators =>
          val team = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId)
          val oppositeTeam = TeamSortingKey(oppositeTeamId, oppositeTeamName, leagueUnitId, leagueUnitName, leagueId)

          val (homeTeam, awayTeam, homeGoals, awayGoals) = if(isHomeMatch == "home") {
            (team, oppositeTeam, goals, enemyGoals)
          } else {
            (oppositeTeam, team, enemyGoals, goals)
          }

        MatchAttendanceOverview(leagueId, homeTeam, awayTeam, homeGoals, awayGoals, matchId, spectators)
    }
  }
}