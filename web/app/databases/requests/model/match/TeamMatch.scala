package databases.requests.model.`match`

import java.util.Date
import ai.x.play.json.Jsonx
import databases.requests.model.team.TeamSortingKey
import anorm.{RowParser, ~}
import anorm.SqlParser.get
import play.api.libs.json.{Json, OFormat}
import ai.x.play.json.implicits._
import ai.x.play.json.{BaseNameEncoder, Jsonx}

case class TeamMatch(season: Int,
                     date: Date,
                     round: Int,
                     homeTeam: TeamSortingKey,
                     awayTeam: TeamSortingKey,
                     matchId: Long,
                     homegoals: Int,
                     awayGoals: Int,
                     homeMatchRatings: MatchRatings,
                     awayMatchRatings: MatchRatings
                    )

case class MatchRatings(formation: String,
                         tacticType: Int,
                         tacticSkill: Int,
                         ratingMidfield: Int,
                         ratingRightDef: Int,
                         ratingMidDef: Int,
                         ratingLeftDef: Int,
                         ratingRightAtt: Int,
                         ratingMidAtt: Int,
                         ratingLeftAtt: Int
                       )

object MatchRatings {
  implicit val encoder: BaseNameEncoder = BaseNameEncoder()
  implicit val writesRatings: OFormat[MatchRatings] = Jsonx.formatCaseClass[MatchRatings]
}

object TeamMatch {
  implicit val encoder: BaseNameEncoder = BaseNameEncoder()
  implicit val sortingKey: OFormat[TeamSortingKey] = Jsonx.formatCaseClass[TeamSortingKey]
  implicit val writes: OFormat[TeamMatch] = Jsonx.formatCaseClass[TeamMatch]

  val mapper: RowParser[TeamMatch] = {
    get[Int]("season") ~
    get[Int]("league_id") ~
    get[Date]("dt") ~
    get[Int]("round") ~
    get[String]("team_name") ~
    get[Long]("team_id") ~
    get[String]("league_unit_name") ~
    get[Long]("league_unit_id") ~
    get[String]("opposite_team_name") ~
    get[Long]("opposite_team_id") ~
    get[Long]("match_id") ~
    get[String]("is_home_match") ~
    get[Int]("goals") ~
    get[Int]("enemy_goals") ~
    get[String]("formation") ~
    get[String]("opposite_formation") ~
    get[Int]("tactic_type") ~
    get[Int]("tactic_skill") ~
    get[Int]("opposite_tactic_type") ~
    get[Int]("opposite_tactic_skill") ~
    get[Int]("rating_midfield") ~
    get[Int]("rating_right_def") ~
    get[Int]("rating_mid_def") ~
    get[Int]("rating_left_def") ~
    get[Int]("rating_right_att") ~
    get[Int]("rating_mid_att") ~
    get[Int]("rating_left_att") ~
    get[Int]("opposite_rating_midfield") ~
    get[Int]("opposite_rating_left_def") ~
    get[Int]("opposite_rating_mid_def") ~
    get[Int]("opposite_rating_right_def") ~
    get[Int]("opposite_rating_left_att") ~
    get[Int]("opposite_rating_mid_att") ~
    get[Int]("opposite_rating_right_att") map {
      case season ~ leagueId ~ date ~ round ~
        teamName ~ teamId ~ leagueUnitName ~ leagueUnitId ~
        oppositeTeamName ~ oppositeTeamId ~ matchId ~ isHomeMatch ~
        goals ~ oppositeGoals ~ formation ~ oppositeFormation ~
        tacticType ~ tacticSkill ~ oppositeTacticType ~ oppositeTacticSkill ~
        ratingMidfield ~ ratingRightDef ~ ratingMidDef ~ ratingLeftDef ~
        ratingRightAtt ~ ratingMidAtt ~ ratingLeftAtt ~
        oppositeRatingMidfield ~ oppositeRatingLeftDef ~ oppositeRatingMidDef ~
        oppositeRatingRightDef ~ oppositeRatingLeftAtt ~ oppositeRatingMidAtt ~
        oppositeRatingRightAtt =>
          val team = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId)
          val oppositeTeam = TeamSortingKey(oppositeTeamId, oppositeTeamName, leagueUnitId, leagueUnitName, leagueId)

          val matchRatings = MatchRatings(formation, tacticType, tacticSkill,
            ratingMidfield, ratingRightDef, ratingMidDef, ratingLeftDef,
            ratingRightAtt, ratingMidAtt, ratingLeftAtt)

          val oppositeMatchRatings = MatchRatings(oppositeFormation, oppositeTacticType, oppositeTacticSkill,
            oppositeRatingMidfield, oppositeRatingLeftDef, oppositeRatingMidDef,
              oppositeRatingRightDef, oppositeRatingLeftAtt, oppositeRatingMidAtt,
              oppositeRatingRightAtt)

          val (homeTeam, awayTeam) = if(isHomeMatch == "home") (team, oppositeTeam) else (oppositeTeam, team)
          val (homeMatchRatings, awayMatchRatings) = if(isHomeMatch == "home") (matchRatings, oppositeMatchRatings) else (oppositeMatchRatings, matchRatings)
          val (homeGoals, awayGoals) = if(isHomeMatch == "home") (goals, oppositeGoals) else (oppositeGoals, goals)

        TeamMatch(season, date, round,
          homeTeam, awayTeam, matchId,
          homeGoals, awayGoals,
          homeMatchRatings, awayMatchRatings)
    }
  }
}
