package databases.requests.model.team

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import databases.requests.model.Chart
import play.api.libs.json.{Json, OWrites}

case class TeamHatstatsChart(teamSortingKey: TeamSortingKey,
                             season: Int,
                             round: Int,
                             hatStats: Int,
                             midfield: Int,
                             defense: Int,
                             attack: Int,
                             loddarStats: Double) extends Chart

object TeamHatstatsChart {
  implicit val writes: OWrites[TeamHatstatsChart] = Json.writes[TeamHatstatsChart]

  val teamRatingMapper: RowParser[TeamHatstatsChart] = {
    get[Int]("league") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("season") ~
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
        season ~
        round ~
        hatstats ~
        midfield ~
        defense ~
        attack ~
        loddarStats =>
        TeamHatstatsChart(
          teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId),
          season = season,
          round = round,
          hatStats = hatstats,
          midfield = midfield,
          defense = defense,
          attack = attack,
          loddarStats = loddarStats)
    }
  }
}
