package databases.requests.model.overview

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import play.api.libs.json.{Json, OWrites}

case class NumberOverview(numberOfTeams:Int,
                          numberOfPlayers: Int,
                          injuried: Int,
                          goals: Int,
                          yellowCards: Int,
                          redCards: Int,
                          numberOfNewTeams: Int)

object NumberOverview {
  implicit val writes: OWrites[NumberOverview] = Json.writes[NumberOverview]

  def apply(numberOverviewPlayerStats: NumberOverviewPlayerStats,
            numberOverviewTeamDetails: NumberOverviewTeamDetails): NumberOverview = {
    NumberOverview(
      numberOfTeams = numberOverviewPlayerStats.numberOfTeams,
      numberOfPlayers = numberOverviewPlayerStats.numberOfPlayers,
      injuried = numberOverviewPlayerStats.injuried,
      goals = numberOverviewPlayerStats.goals,
      yellowCards = numberOverviewPlayerStats.yellowCards,
      redCards = numberOverviewPlayerStats.redCards,
      numberOfNewTeams = numberOverviewTeamDetails.numberOfNewTeams
    )
  }
}

case class NumberOverviewPlayerStats(numberOfTeams:Int,
                                     numberOfPlayers: Int,
                                     injuried: Int,
                                     goals: Int,
                                     yellowCards: Int,
                                     redCards: Int)

object NumberOverviewPlayerStats {
  implicit val writes: OWrites[NumberOverviewPlayerStats] = Json.writes[NumberOverviewPlayerStats]

  val mapper: RowParser[NumberOverviewPlayerStats] = {
    get[Int]("numberOfTeams") ~
      get[Int]("numberOfPlayers") ~
      get[Int]("injuried") ~
      get[Int]("goals") ~
      get[Int]("yellow_cards") ~
      get[Int]("red_cards") map {
      case numberOfTeams ~ numberOfPlayers ~ injuried ~ goals ~ yellowCards ~ redCards =>
        NumberOverviewPlayerStats(numberOfTeams, numberOfPlayers, injuried, goals, yellowCards, redCards)
    }
  }
}

case class NumberOverviewTeamDetails(numberOfNewTeams: Int)

object NumberOverviewTeamDetails {
  implicit val writes: OWrites[NumberOverviewTeamDetails] = Json.writes[NumberOverviewTeamDetails]

  val mapper: RowParser[NumberOverviewTeamDetails] = {
    get[Int]("numberOfNewTeams") map {
      case numberOfNewTeams => NumberOverviewTeamDetails(numberOfNewTeams)
    }
  }
}
