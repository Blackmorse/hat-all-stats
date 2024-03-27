package databases.requests.model.`match`

import anorm.SqlParser.get
import anorm.~
import databases.requests.model.team.TeamSortingKey
import play.api.libs.json.{Json, OWrites}

case class MatchSpectators(homeTeam: TeamSortingKey,
                           awayTeam: TeamSortingKey,
                           homeGoals: Int,
                           awayGoals: Int,
                           spectators: Int,
                           matchId: Long)

object MatchSpectators {
  implicit val writes: OWrites[MatchSpectators] = Json.writes[MatchSpectators]

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
    get[Int]("sold_total") map {
      case leagueId ~ leagueUnitId ~ leagueUnitName ~ teamId ~ teamName ~ oppositeTeamId
        ~ oppositeTeamName ~ matchId ~ isHomeMatch ~ goals ~ enemyGoals ~ soldTotal =>

        val (homeTeam, awayTeam, homeGoals, awayGoals) =
          if (isHomeMatch == "home") {
            (TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId),
              TeamSortingKey(oppositeTeamId, oppositeTeamName, leagueUnitId, leagueUnitName, leagueId),
              goals, enemyGoals)
          } else {
            (TeamSortingKey(oppositeTeamId, oppositeTeamName, leagueUnitId, leagueUnitName, leagueId),
              TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId),
              enemyGoals, goals)
          }

        MatchSpectators(homeTeam, awayTeam, homeGoals, awayGoals, soldTotal, matchId)
    }
  }
}
