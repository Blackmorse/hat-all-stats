package com.blackmorse.hattid.web.databases.requests.model.overview

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class OverviewMatchAverages(hatstats: Int,
                                 spectators: Int,
                                 goals: Double)

object OverviewMatchAverages {
  implicit val jsonEncoder: JsonEncoder[OverviewMatchAverages] = DeriveJsonEncoder.gen[OverviewMatchAverages]

  val mapper: RowParser[OverviewMatchAverages] = {
    get[Int]("avg_hatstats") ~
    get[Int]("avg_sold_total") ~
    get[Double]("avg_goals") map {
      case avgHatstats ~ avgSoldTickets ~ avgScoredByTeam =>
        OverviewMatchAverages(avgHatstats, avgSoldTickets, if(avgScoredByTeam.isNaN || avgScoredByTeam.isInfinite) null.asInstanceOf[Double] else avgScoredByTeam)
    }
  }
}

case class OverviewTeamPlayerAverages(age: Int,
                                      salary: Int,
                                      rating: Double)

object OverviewTeamPlayerAverages {
  implicit val jsonEncoder: JsonEncoder[OverviewTeamPlayerAverages] = DeriveJsonEncoder.gen[OverviewTeamPlayerAverages]

  val mapper: RowParser[OverviewTeamPlayerAverages] = {
    get[Int]("avg_age") ~
    get[Int]("avg_salary") ~
    get[Double]("avg_rating") map {
      case averageAge ~ averageSalary ~ averateRating =>
        OverviewTeamPlayerAverages(averageAge, averageSalary, if(averateRating.isNaN || averateRating.isInfinite) null.asInstanceOf[Double] else averateRating)
    }
  }
}

case class AveragesOverview(matchAverages: OverviewMatchAverages,
                            teamPlayerAverages: OverviewTeamPlayerAverages)

object AveragesOverview {
  implicit val jsonEncoder: JsonEncoder[AveragesOverview] = DeriveJsonEncoder.gen[AveragesOverview]
}