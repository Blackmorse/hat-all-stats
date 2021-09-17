package databases.requests.matchdetails

import anorm.RowParser
import chpp.matchdetails.models.MatchDetails
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest
import databases.requests.model.`match`.SimilarMatchesStats
import databases.sqlbuilder.{NestedSelect, Select, SqlBuilder}

import scala.concurrent.Future

object SimilarMatchesRequest extends ClickhouseRequest[SimilarMatchesStats] {
  override val rowParser: RowParser[SimilarMatchesStats] = SimilarMatchesStats.mapper

  def execute(matchDetails: MatchDetails, accuracy: Double)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[Option[SimilarMatchesStats]] = {

    val matc = matchDetails.matc
    import SqlBuilder.implicits._
    val builder = Select(
        "countIf(goals > enemy_goals)" as "wins",
        "countIf(goals = enemy_goals)" as "draws",
        "countIf(goals < enemy_goals)" as "loses",
        "count()" as "count",
        "if(count = 0, 0, avg(goals))" as "avg_goals_for",
        "if(count = 0, 0, avg(enemy_goals))" as "avg_goals_against"
      ).from(
        NestedSelect(
            "match_id", "goals", "enemy_goals",
            rates(matc.homeTeam.ratingMidfield, matc.awayTeam.ratingMidfield, "rating_midfield", "opposite_rating_midfield") as "mids",
            rates(matc.homeTeam.ratingMidDef, matc.awayTeam.ratingMidAtt, "rating_mid_def", "opposite_rating_mid_att") as "mid_def_mid_att",
            rates(matc.homeTeam.ratingMidAtt, matc.awayTeam.ratingMidDef, "rating_mid_att", "opposite_rating_mid_def") as "mid_att_mid_def",

            rates(matc.homeTeam.ratingLeftDef, matc.awayTeam.ratingRightAtt, "rating_left_def", "opposite_rating_right_att") as "left_def_right_att",
            rates(matc.homeTeam.ratingRightDef, matc.awayTeam.ratingLeftAtt, "rating_right_def", "opposite_rating_left_att") as "right_def_left_att",
            rates(matc.homeTeam.ratingRightAtt, matc.awayTeam.ratingLeftDef, "rating_right_att", "opposite_rating_left_def") as "right_att_left_def",
            rates(matc.homeTeam.ratingLeftAtt, matc.awayTeam.ratingRightDef, "rating_left_att", "opposite_rating_right_def") as "left_att_right_def",

            rates(matc.homeTeam.ratingRightDef, matc.awayTeam.ratingLeftAtt, "rating_left_def", "opposite_rating_right_att") as "left_def_right_att_rev",
            rates(matc.homeTeam.ratingLeftDef, matc.awayTeam.ratingRightAtt, "rating_right_def", "opposite_rating_left_att") as "right_def_left_att_rev",
            rates(matc.homeTeam.ratingLeftAtt, matc.awayTeam.ratingRightDef, "rating_right_att", "opposite_rating_left_def") as "right_att_left_def_rev",
            rates(matc.homeTeam.ratingRightAtt, matc.awayTeam.ratingLeftDef, "rating_left_att", "opposite_rating_right_def") as "left_att_right_def_rev",

            rates(matc.homeTeam.ratingIndirectSetPiecesDef, matc.awayTeam.ratingIndirectSetPiecesAtt, "rating_indirect_set_pieces_def", "opposite_rating_indirect_set_pieces_att") as "indir_def_att",
            rates(matc.homeTeam.ratingIndirectSetPiecesAtt, matc.awayTeam.ratingIndirectSetPiecesDef, "rating_indirect_set_pieces_att", "opposite_rating_indirect_set_pieces_def") as "indir_att_def"
          ).from("hattrick.match_details")
          .where
          .and(s"match_id != ${matc.matchId}")

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

    restClickhouseDAO.executeSingleOpt(builder.build, rowParser)
  }

  private def rates(value1: Int, value2: Int, field1: String, field2: String): String = {
    s""" (pow($value1 / 4, 3) / (pow($value1 / 4, 3) + pow($value2 / 4, 3))) - (pow($field1 / 4, 3) / (pow($field1 / 4, 3) + pow($field2 / 4, 3))) """
  }
}
