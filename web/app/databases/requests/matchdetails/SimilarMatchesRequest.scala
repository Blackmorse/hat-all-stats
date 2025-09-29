package databases.requests.matchdetails

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest
import databases.requests.ClickhouseRequest.DBIO
import databases.requests.model.`match`.SimilarMatchesStats
import models.web.matches.SingleMatch
import sqlbuilder.{NestedSelect, Select}
import zio.ZIO

object SimilarMatchesRequest extends ClickhouseRequest[SimilarMatchesStats] {
  override val rowParser: RowParser[SimilarMatchesStats] = SimilarMatchesStats.mapper

  def execute(singleMatch: SingleMatch, accuracy: Double): DBIO[Option[SimilarMatchesStats]] = wrapErrorsOpt {
    val homeTeamRatings = singleMatch.homeMatchRatings
    val awayTeamRatings = singleMatch.awayMatchRatings

    import sqlbuilder.SqlBuilder.implicits._
    val builder = Select(
        "countIf(goals > enemy_goals)" `as` "wins",
        "countIf(goals = enemy_goals)" `as` "draws",
        "countIf(goals < enemy_goals)" `as` "loses",
        "count()" `as` "count",
        "if(count = 0, 0, avg(goals))" `as` "avg_goals_for",
        "if(count = 0, 0, avg(enemy_goals))" `as` "avg_goals_against"
      ).from(
        NestedSelect(
            "match_id", "goals", "enemy_goals",
            rates(homeTeamRatings.ratingMidfield, awayTeamRatings.ratingMidfield, "rating_midfield", "opposite_rating_midfield") `as` "mids",
            rates(homeTeamRatings.ratingMidDef, awayTeamRatings.ratingMidAtt, "rating_mid_def", "opposite_rating_mid_att") `as` "mid_def_mid_att",
            rates(homeTeamRatings.ratingMidAtt, awayTeamRatings.ratingMidDef, "rating_mid_att", "opposite_rating_mid_def") `as` "mid_att_mid_def",

            rates(homeTeamRatings.ratingLeftDef, awayTeamRatings.ratingRightAtt, "rating_left_def", "opposite_rating_right_att") `as` "left_def_right_att",
            rates(homeTeamRatings.ratingRightDef, awayTeamRatings.ratingLeftAtt, "rating_right_def", "opposite_rating_left_att") `as` "right_def_left_att",
            rates(homeTeamRatings.ratingRightAtt, awayTeamRatings.ratingLeftDef, "rating_right_att", "opposite_rating_left_def") `as` "right_att_left_def",
            rates(homeTeamRatings.ratingLeftAtt, awayTeamRatings.ratingRightDef, "rating_left_att", "opposite_rating_right_def") `as` "left_att_right_def",

            rates(homeTeamRatings.ratingRightDef, awayTeamRatings.ratingLeftAtt, "rating_left_def", "opposite_rating_right_att") `as` "left_def_right_att_rev",
            rates(homeTeamRatings.ratingLeftDef, awayTeamRatings.ratingRightAtt, "rating_right_def", "opposite_rating_left_att") `as` "right_def_left_att_rev",
            rates(homeTeamRatings.ratingLeftAtt, awayTeamRatings.ratingRightDef, "rating_right_att", "opposite_rating_left_def") `as` "right_att_left_def_rev",
            rates(homeTeamRatings.ratingRightAtt, awayTeamRatings.ratingLeftDef, "rating_left_att", "opposite_rating_right_def") `as` "left_att_right_def_rev",

            rates(homeTeamRatings.ratingIndirectSetPiecesDef, awayTeamRatings.ratingIndirectSetPiecesAtt, "rating_indirect_set_pieces_def", "opposite_rating_indirect_set_pieces_att") `as` "indir_def_att",
            rates(homeTeamRatings.ratingIndirectSetPiecesAtt, awayTeamRatings.ratingIndirectSetPiecesDef, "rating_indirect_set_pieces_att", "opposite_rating_indirect_set_pieces_def") `as` "indir_att_def"
          ).from("hattrick.match_details")
          .where
          .and(singleMatch.matchId.map(matchId => s"match_id != $matchId"))
          .and(s"abs(indir_def_att) < 2 * $accuracy and abs(indir_att_def) < 2 * $accuracy")

          .and(s"abs(mids) < ($accuracy / 2)")
          .and(s"abs(mid_def_mid_att) < $accuracy and abs(mid_att_mid_def) < $accuracy")
          .and(s"abs(mid_def_mid_att + mid_att_mid_def) < $accuracy")
          .and(
            s"""
               |(
               |  (
               |    abs(left_def_right_att) < $accuracy and abs(right_def_left_att) < $accuracy and
               |    abs(right_att_left_def) < $accuracy and abs(left_att_right_def) < $accuracy and
               |    abs(left_def_right_att + right_att_left_def) < $accuracy and abs(right_def_left_att + left_att_right_def) < $accuracy
               |  )
               |  OR
               |  (
               |    abs(left_def_right_att_rev) < $accuracy and abs(right_def_left_att_rev) < $accuracy and
               |    abs(right_att_left_def_rev) < $accuracy and abs(left_att_right_def_rev) < $accuracy and
               |    abs(left_def_right_att_rev + right_att_left_def_rev) < $accuracy and abs(right_def_left_att_rev + left_att_right_def_rev) < $accuracy
               |  )
               |)""".stripMargin)
    )

    for {
      restClickhouseDAO <- ZIO.service[RestClickhouseDAO]
      result <- restClickhouseDAO.executeSingleOptZIO(builder.sqlWithParameters().build, rowParser)
    } yield result

  }

  private def rates(value1: Int, value2: Int, field1: String, field2: String): String = {
    s""" (pow($value1 / 4, 3) / (pow($value1 / 4, 3) + pow($value2 / 4, 3))) - (pow($field1 / 4, 3) / (pow($field1 / 4, 3) + pow($field2 / 4, 3))) """
  }
}
