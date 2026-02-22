package com.blackmorse.hattid.web.databases.requests.model.team

import java.util.Date
import anorm.SqlParser.get
import anorm.{RowParser, ~}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class CreatedSameTimeTeam(teamSortingKey: TeamSortingKey,
                               hatstats: Int,
                               attack: Int,
                               midfield: Int,
                               defense: Int,
                               loddarStats: Double,
                               tsi: Int,
                               salary: Int,
                               rating: Int,
                               ratingEndOfMatch: Int,
                               age: Double,
                               injury: Int,
                               foundedDate: Date,
                               powerRating: Int)

object CreatedSameTimeTeam {
  implicit val jsonEncoder: JsonEncoder[CreatedSameTimeTeam] = DeriveJsonEncoder.gen[CreatedSameTimeTeam]
  implicit val dateEncoder: JsonEncoder[Date] = JsonEncoder[Long].contramap(_.getTime)

  val createdSameTimeTeamMapper: RowParser[CreatedSameTimeTeam] = {
    get[Int]("league_id") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("hatstats") ~
    get[Int]("attack") ~
    get[Int]("midfield") ~
    get[Int]("defense") ~
    get[Double]("loddar_stats") ~
    get[Int]("tsi") ~
    get[Int]("salary") ~
    get[Int]("rating") ~
    get[Int]("rating_end_of_match") ~
    get[Double]("age") ~
    get[Int]("injury") ~
    get[Date]("founded") ~
    get[Int]("power_rating") map {
      case leagueId ~
        teamId ~
        teamName ~
        leagueUnitId ~
        leagueUnitName ~
        hatstats ~
        attack ~
        midfield ~
        defense ~
        loddarStats ~
        tsi ~
        salary ~
        rating ~
        ratingEndOfMatch ~
        age ~
        injury ~
        foundedDate ~
        powerRating =>
        CreatedSameTimeTeam(
          teamSortingKey = TeamSortingKey(
            teamId = teamId,
            teamName = teamName,
            leagueUnitId = leagueUnitId,
            leagueUnitName = leagueUnitName,
            leagueId = leagueId
          ),
          hatstats = hatstats,
          attack = attack,
          midfield = midfield,
          defense = defense,
          loddarStats = loddarStats,
          tsi = tsi,
          salary = salary,
          rating = rating,
          ratingEndOfMatch = ratingEndOfMatch,
          age = age,
          injury = injury,
          foundedDate = foundedDate,
          powerRating = powerRating
        )
    }
  }
}
