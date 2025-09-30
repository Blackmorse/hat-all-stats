package databases.requests.model.team

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import play.api.libs.json.{Json, OWrites}

case class TeamHatstatsChart(teamSortingKey: TeamSortingKey,
                        hatStats: Int, midfield: Int, defense: Int, attack: Int,
                        loddarStats: Double)

object TeamHatstatsChart {
  implicit val writes: OWrites[TeamHatstatsChart] = Json.writes[TeamHatstatsChart]

  val teamRatingMapper: RowParser[TeamHatstatsChart] = {
    get[Int]("league") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("round") ~
      get[Int]("hatstats") ~
      get[Int]("midfield") ~
      get[Int]("defense") ~
      get[Int]("attack") ~
      get[Double]("loddar_stats") map {
      case leagueId ~
        teamId ~
        teamName ~
        leagueUnitId ~
        leagueUnitName ~
        round ~
        hatstats ~
        midfield ~
        defense ~
        attack ~
        loddarStats =>
        TeamHatstatsChart(
          teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId),
          hatStats = hatstats,
          midfield = midfield,
          defense = defense,
          attack = attack,
          loddarStats = loddarStats)
    }
  }
}
