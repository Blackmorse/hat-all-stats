package models.clickhouse.overview

import anorm.SqlParser.get
import anorm.~

case class AvgTeamPlayersStats(averageAge: Int, averageSalary: Int, averageRating: Double)

object AvgTeamPlayersStats {
  val mapper = {
    get[Int]("avg_age") ~
    get[Int]("avg_salary") ~
    get[Double]("avg_rating") map {
      case averageAge ~ averageSalary ~ averateRating =>
        AvgTeamPlayersStats(averageAge, averageSalary, averateRating)
    }
  }
}
