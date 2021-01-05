package models.clickhouse

import anorm.SqlParser.get
import anorm.~
import ai.x.play.json.{BaseNameEncoder, Jsonx}
import play.api.libs.json.{Json, OFormat}
import ai.x.play.json.implicits._

case class TeamRankings(teamId: Long,
                        teamName: String,
                        divisionLevel: Int,
                        round: Int,
                        rank_type: String,
                        hatstats: Int,
                        hatstatsPosition: Int,
                        attack: Int,
                        attackPosition: Int,
                        midfield: Int,
                        midfieldPosition: Int,
                        defense: Int,
                        defensePosition: Int,
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
                        powerRatingPosition: Int
                       )

object TeamRankings {
  implicit val encoder = BaseNameEncoder()
  implicit val writes: OFormat[TeamRankings] = Jsonx.formatCaseClass[TeamRankings]

  val teamRankingsMapper = {
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Int]("division_level") ~
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
    get[Int]("power_rating_position") map {
      case teamId ~ teamName ~ divisionLevel ~ round ~ rankType ~ hatstats ~ hatstatsPosition ~ attack ~
        attackPosition ~ midfield ~ midfieldPosition ~
        defense ~ defensePosition ~ tsi ~ tsiPosition ~ salary ~ salaryPosition ~
        rating ~ ratingPosition ~ ratingEndOfMatch ~ ratingEndOfMatchPosition ~
        age ~ agePosition ~ injury ~ injuryPosition ~ injuryCount ~ injuryCountPosition  ~
        powerRating ~ powerRatingPosition =>
        TeamRankings(teamId, teamName, divisionLevel, round, rankType, hatstats, hatstatsPosition, attack,
          attackPosition, midfield, midfieldPosition, defense, defensePosition,
          tsi, tsiPosition, salary, salaryPosition, rating, ratingPosition,
          ratingEndOfMatch, ratingEndOfMatchPosition, age, agePosition,
          injury, injuryPosition, injuryCount, injuryCountPosition,
          powerRating, powerRatingPosition
        )
    }
  }
}
