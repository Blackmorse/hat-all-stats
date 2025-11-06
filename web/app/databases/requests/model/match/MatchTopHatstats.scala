package databases.requests.model.`match`

import anorm.{RowParser, ~}
import anorm.SqlParser.get
import databases.requests.model.team.TeamSortingKey
import play.api.libs.json.{Json, OWrites}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class MatchTopHatstats(
                             leagueId: Int,
                             homeTeam: TeamSortingKey,
                             awayTeam: TeamSortingKey,
                             homeHatstats: Int,
                             homeGoals: Int,
                             homeLoddarStats: Double,
                             awayHatstats: Int,
                             awayGoals: Int,
                             awayLoddarStats: Double,
                             matchId: Long)

object MatchTopHatstats {
  implicit val writes: OWrites[MatchTopHatstats] = Json.writes[MatchTopHatstats]
  implicit val jsonEncoder: JsonEncoder[MatchTopHatstats] = DeriveJsonEncoder.gen[MatchTopHatstats]

  val mapper: RowParser[MatchTopHatstats] = {
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
    get[Int]("opposite_hatstats") ~
    get[Double]("loddar_stats") ~
    get[Double]("opposite_loddar_stats") map {
      case leagueId ~ leagueUnitId ~ leagueUnitName ~ teamId ~ teamName ~ oppositeTeamId
        ~ oppositeTeamName ~ matchId ~ isHomeMatch ~ goals ~ enemyGoals
        ~ hatstats ~ oppositeHatstats ~ loddarStats ~ oppositeLoddarStats =>

        MatchTopHatstats(leagueId, leagueUnitId, leagueUnitName, teamId, teamName, oppositeTeamId,
          oppositeTeamName, matchId, isHomeMatch, goals, enemyGoals,
          hatstats, oppositeHatstats, loddarStats, oppositeLoddarStats)
    }
  }

  def apply(leagueId: Int, leagueUnitId: Long, leagueUnitName: String, teamId: Long, teamName: String, oppositeTeamId: Long,
    oppositeTeamName: String, matchId: Long, isHomeMatch: String, goals: Int, enemyGoals: Int,
    hatstats: Int, oppositeHatstats: Int, loddarStats: Double, oppositeLoddarStats: Double): MatchTopHatstats = {
    val (homeTeam, awayTeam, homeHatstats, awayHatstats, homeGoals, awayGoals, homeLoddarStats, awayLoddarStats) =
      if (isHomeMatch == "home") {
        (TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId),
          TeamSortingKey(oppositeTeamId, oppositeTeamName, leagueUnitId, leagueUnitName, leagueId),
          hatstats, oppositeHatstats,
          goals, enemyGoals,
          loddarStats, oppositeLoddarStats)
      } else {
        (TeamSortingKey(oppositeTeamId, oppositeTeamName, leagueUnitId, leagueUnitName, leagueId),
          TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId),
          oppositeHatstats, hatstats,
          enemyGoals, goals,
          oppositeLoddarStats, loddarStats)
      }

    MatchTopHatstats(
      leagueId = leagueId,
      homeTeam = homeTeam,
      awayTeam = awayTeam,
      homeHatstats = homeHatstats,
      homeGoals = homeGoals,
      homeLoddarStats = homeLoddarStats,
      awayHatstats = awayHatstats,
      awayGoals = awayGoals,
      awayLoddarStats = awayLoddarStats,
      matchId = matchId)
  }
}
