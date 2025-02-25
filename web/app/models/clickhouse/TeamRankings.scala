package models.clickhouse

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import play.api.libs.json.{Json, OFormat, OWrites}

import java.util.Date

case class TeamRankings(teamId: Long,
                        teamName: String,
                        divisionLevel: Int,
                        season: Int,
                        round: Int,
                        rankType: String,
                        hatstats: Int,
                        hatstatsPosition: Int,
                        attack: Int,
                        attackPosition: Int,
                        midfield: Int,
                        midfieldPosition: Int,
                        defense: Int,
                        defensePosition: Int,
                        loddarStats: Double,
                        loddarStatsPosition: Int,
                        tsi: Int,
                        tsiPosition: Int,
                        salary: Int,
                        salaryPosition: Int,
                        rating: Int,
                        ratingPosition: Int,
                        ratingEndOfMatch: Int,
                        ratingEndOfMatchPosition: Int,
                        age: Int,
                        agePosition: Int,
                        injury: Int,
                        injuryPosition: Int,
                        injuryCount: Int,
                        injuryCountPosition: Int,
                        powerRating: Int,
                        powerRatingPosition: Int,
                        founded: Date,
                        foundedPosition: Int
                       )

object TeamRankings {
  implicit val writes: OWrites[TeamRankings] = Json.writes[TeamRankings]

  val teamRankingsMapper: RowParser[TeamRankings] = {
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Int]("division_level") ~
    get[Int]("season") ~
    get[Int]("round") ~
    get[String]("rank_type") ~
    get[Int]("hatstats") ~
    get[Int]("hatstats_position") ~
    get[Int]("attack") ~
    get[Int]("attack_position") ~
    get[Int]("midfield") ~
    get[Int]("midfield_position") ~
    get[Int]("defense") ~
    get[Int]("defense_position") ~
    get[Double]("loddar_stats") ~
    get[Int]("loddar_stats_position") ~
    get[Int]("tsi") ~
    get[Int]("tsi_position") ~
    get[Int]("salary") ~
    get[Int]("salary_position") ~
    get[Int]("rating") ~
    get[Int]("rating_position") ~
    get[Int]("rating_end_of_match") ~
    get[Int]("rating_end_of_match_position") ~
    get[Int]("age") ~
    get[Int]("age_position") ~
    get[Int]("injury") ~
    get[Int]("injury_position") ~
    get[Int]("injury_count") ~
    get[Int]("injury_count_position") ~
    get[Int]("power_rating") ~
    get[Int]("power_rating_position") ~
    get[Date]("founded") ~
    get[Int]("founded_position") map {
      case teamId ~ teamName ~ divisionLevel ~ season ~ round ~ rankType ~ hatstats ~ hatstatsPosition ~ attack ~
        attackPosition ~ midfield ~ midfieldPosition ~
        defense ~ defensePosition ~ loddarStats ~ loddarStatsPosition ~ tsi ~ tsiPosition ~ salary ~ salaryPosition ~
        rating ~ ratingPosition ~ ratingEndOfMatch ~ ratingEndOfMatchPosition ~
        age ~ agePosition ~ injury ~ injuryPosition ~ injuryCount ~ injuryCountPosition  ~
        powerRating ~ powerRatingPosition ~ founded ~ foundedPosition =>
        TeamRankings(teamId = teamId,
          teamName = teamName,
          divisionLevel = divisionLevel,
          season = season,
          round = round,
          rankType = rankType,
          hatstats = hatstats,
          hatstatsPosition = hatstatsPosition,
          attack = attack,
          attackPosition = attackPosition,
          midfield = midfield,
          midfieldPosition = midfieldPosition,
          defense = defense,
          defensePosition = defensePosition,
          loddarStats = loddarStats,
          loddarStatsPosition = loddarStatsPosition,
          tsi = tsi,
          tsiPosition = tsiPosition,
          salary = salary,
          salaryPosition = salaryPosition,
          rating = rating,
          ratingPosition = ratingPosition,
          ratingEndOfMatch = ratingEndOfMatch,
          ratingEndOfMatchPosition = ratingEndOfMatchPosition,
          age = age,
          agePosition = agePosition,
          injury = injury,
          injuryPosition = injuryPosition,
          injuryCount = injuryCount,
          injuryCountPosition = injuryCountPosition,
          powerRating = powerRating,
          powerRatingPosition = powerRatingPosition,
          founded = founded,
          foundedPosition = foundedPosition
        )
    }
  }
}
