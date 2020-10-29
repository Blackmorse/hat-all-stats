package databases.requests.model.team

import play.api.libs.json.Json
import anorm.SqlParser.get
import anorm.~

case class TeamGoalPoints(teamSortingKey: TeamSortingKey,
                          won: Int,
                          lost: Int,
                          draw: Int,
                          goalsFor: Int,
                          goalsAgaints: Int,
                          goalsDifference: Int,
                          points: Int)

object TeamGoalPoints {
  implicit val writes = Json.writes[TeamGoalPoints]

  val mapper = {
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("won") ~
    get[Int]("lost") ~
    get[Int]("draw") ~
    get[Int]("goals_for") ~
    get[Int]("goals_against") ~
    get[Int]("goals_difference") ~
    get[Int]("points") map {
      case teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
        won ~ lost ~ draw ~ goalsFor ~ goalsAgainst ~
        goalsDifference ~ points =>
        val teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName)

        TeamGoalPoints(teamSortingKey, won, lost, draw, goalsFor, goalsAgainst, goalsDifference, points)
    }
  }
}
