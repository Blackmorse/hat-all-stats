package databases.requests.matchdetails

import anorm.RowParser
import com.blackmorse.hattrick.api.matchdetails.model.MatchDetails
import databases.{RestClickhouseDAO, SqlBuilder}
import databases.requests.ClickhouseRequest
import databases.requests.model.`match`.SimilarMatchesStats

import scala.concurrent.Future

object SimilarMatchesRequest extends ClickhouseRequest[SimilarMatchesStats] {
  override val rowParser: RowParser[SimilarMatchesStats] = SimilarMatchesStats.mapper

  def execute(matchDetails: MatchDetails, accuracy: Double)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[Option[SimilarMatchesStats]] = {
    val matc = matchDetails.getMatch
    val request = s"""select
               |countIf(goals > enemy_goals) as wins,
               |countIf(goals = enemy_goals) as draws,
               |countIf(goals < enemy_goals) as loses,
               |avg(goals) as avg_goals_for,
               |avg(enemy_goals) as avg_goals_against,
               |count() as count
               |from (
               |select match_id, goals, enemy_goals,
               |${rates(matc.getHomeTeam.getRatingMidfield, matc.getAwayTeam.getRatingMidfield, "rating_midfield", "opposite_rating_midfield")} as mids,
               |${rates(matc.getHomeTeam.getRatingMidDef, matc.getAwayTeam.getRatingMidAtt, "rating_mid_def", "opposite_rating_mid_att")} as mid_def_mid_att,
               |${rates(matc.getHomeTeam.getRatingMidAtt, matc.getAwayTeam.getRatingMidDef, "rating_mid_att", "opposite_rating_mid_def")} as mid_att_mid_def,
               |
               |${rates(matc.getHomeTeam.getRatingLeftDef, matc.getAwayTeam.getRatingRightAtt, "rating_left_def", "opposite_rating_right_att")} as left_def_right_att,
               |${rates(matc.getHomeTeam.getRatingRightDef, matc.getAwayTeam.getRatingLeftAtt, "rating_right_def", "opposite_rating_left_att")} as right_def_left_att,
               |${rates(matc.getHomeTeam.getRatingRightAtt, matc.getAwayTeam.getRatingLeftDef, "rating_right_att", "opposite_rating_left_def")} as right_att_left_def,
               |${rates(matc.getHomeTeam.getRatingLeftAtt, matc.getAwayTeam.getRatingRightDef, "rating_left_att", "opposite_rating_right_def")} as left_att_right_def,
               |
               |${rates(matc.getHomeTeam.getRatingRightDef, matc.getAwayTeam.getRatingLeftAtt, "rating_left_def", "opposite_rating_right_att")} as left_def_right_att_rev,
               |${rates(matc.getHomeTeam.getRatingLeftDef, matc.getAwayTeam.getRatingRightAtt, "rating_right_def", "opposite_rating_left_att")} as right_def_left_att_rev,
               |${rates(matc.getHomeTeam.getRatingLeftAtt, matc.getAwayTeam.getRatingRightDef, "rating_right_att", "opposite_rating_left_def")} as right_att_left_def_rev,
               |${rates(matc.getHomeTeam.getRatingRightAtt, matc.getAwayTeam.getRatingLeftDef, "rating_left_att", "opposite_rating_right_def")} as left_att_right_def_rev,
               |
               |${rates(matc.getHomeTeam.getRatingIndirectSetPiecesDef, matc.getAwayTeam.getRatingIndirectSetPiecesAtt, "rating_indirect_set_pieces_def", "opposite_rating_indirect_set_pieces_att")} as indir_def_att,
               |${rates(matc.getHomeTeam.getRatingIndirectSetPiecesAtt, matc.getAwayTeam.getRatingIndirectSetPiecesDef, "rating_indirect_set_pieces_att", "opposite_rating_indirect_set_pieces_def")} as indir_att_def
               |
               |
               |from hattrick.match_details
               |where  match_id != ${matc.getMatchId} and

               |abs(indir_def_att) < 2 * $accuracy and abs(indir_att_def) < 2 * $accuracy
               |and
               |
               |abs(mids) < ($accuracy / 2) and
               |abs(mid_def_mid_att) < $accuracy and abs(mid_att_mid_def) < $accuracy and abs(mid_def_mid_att + mid_att_mid_def) < $accuracy
               |and
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
               |))
    """.stripMargin

    restClickhouseDAO.executeSingleOpt(SqlBuilder(request).build, rowParser)
  }

  private def rates(value1: Int, value2: Int, field1: String, field2: String): String = {
    s""" (pow($value1 / 4, 3) / (pow($value1 / 4, 3) + pow($value2 / 4, 3))) - (pow($field1 / 4, 3) / (pow($field1 / 4, 3) + pow($field2 / 4, 3))) """
  }
}
