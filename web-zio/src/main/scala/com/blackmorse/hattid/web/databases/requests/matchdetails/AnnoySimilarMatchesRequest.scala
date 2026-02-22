package com.blackmorse.hattid.web.databases.requests.matchdetails

import anorm.RowParser
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.DBIO
import com.blackmorse.hattid.web.databases.requests.model.`match`.SimilarMatchesStats
import com.blackmorse.hattid.web.models.web.matches.SingleMatch
import sqlbuilder.{NestedSelect, Select}

import sqlbuilder.functions.{If, avg, countIf}
import zio.ZIO

object AnnoySimilarMatchesRequest extends ClickhouseRequest[SimilarMatchesStats] {
  override val rowParser: RowParser[SimilarMatchesStats] = SimilarMatchesStats.mapper

  def execute(singleMatch: SingleMatch, 
              accuracy: Int,
              considerTacticType: Boolean,
              considerTacticSkill: Boolean,
              considerSetPiecesLevels: Boolean): DBIO[Option[SimilarMatchesStats]] = wrapErrorsOpt {
    val homeTeamRatings = singleMatch.homeMatchRatings
    val awayTeamRatings = singleMatch.awayMatchRatings

    val accuracyForTacticDifference = 12 - accuracy
    val considerTacticTypeCondition = if(considerTacticType) Some(s"(tactic_type = ${homeTeamRatings.tacticType} AND opposite_tactic_type = ${awayTeamRatings.tacticType})") else None
    val considerTacticSkillCondition = if(considerTacticSkill) Some(s"abs(abs(tactic_skill - opposite_tactic_skill) - abs(${awayTeamRatings.tacticSkill} - ${homeTeamRatings.tacticSkill})) < $accuracyForTacticDifference") else None

    val accuracyForVectorDifference = 0.11d - (accuracy.toDouble / 100)
    val considerSetPiecesCondition = if(considerSetPiecesLevels) Some(s"L2Distance([rates(${homeTeamRatings.ratingIndirectSetPiecesDef}, ${awayTeamRatings.ratingIndirectSetPiecesAtt}), rates(${homeTeamRatings.ratingIndirectSetPiecesAtt}, ${awayTeamRatings.ratingIndirectSetPiecesDef})], [rates(rating_indirect_set_pieces_def, opposite_rating_indirect_set_pieces_att), rates(rating_indirect_set_pieces_att, opposite_rating_indirect_set_pieces_def)]) <= $accuracyForVectorDifference * 2") else None

    import sqlbuilder.SqlBuilder.implicits._
    val builder = Select(
      countIf("goals > enemy_goals").as("wins"),
      countIf("goals < enemy_goals").as("loses"),
      countIf("goals = enemy_goals").as("draws"),
      "count()".as("count"),
      If("count = 0", "0", avg("goals")).as("avg_goals_for"),
      If("count = 0", "0", avg("enemy_goals")).as("avg_goals_against")
    ).from(
        NestedSelect(
          "goals", "enemy_goals", 
          s"""L2Distance([
                rates(${homeTeamRatings.ratingMidfield}, ${awayTeamRatings.ratingMidfield}),
                rates(${homeTeamRatings.ratingLeftDef}, ${awayTeamRatings.ratingRightAtt}),
                rates(${homeTeamRatings.ratingMidDef}, ${awayTeamRatings.ratingMidAtt}),
                rates(${homeTeamRatings.ratingRightDef}, ${awayTeamRatings.ratingLeftAtt})
            ], vector
            )""".as("dist"),
          s"""L2Distance([
                rates(${homeTeamRatings.ratingMidfield}, ${awayTeamRatings.ratingMidfield}),
                rates(${homeTeamRatings.ratingRightDef}, ${awayTeamRatings.ratingLeftAtt}),
                rates(${homeTeamRatings.ratingMidDef}, ${awayTeamRatings.ratingMidAtt}),
                rates(${homeTeamRatings.ratingLeftDef}, ${awayTeamRatings.ratingRightAtt})
            ], vector
            )""".as("dist_mirror")
        ).from("hattrick.match_details_annoy")
          .where
          .and(s"(dist <= $accuracyForVectorDifference OR dist_mirror <= $accuracyForVectorDifference)")
          .and(singleMatch.matchId.map(matchId => s"match_id != $matchId"))
          .and(considerTacticTypeCondition)
          .and(considerTacticSkillCondition)
          .and(considerSetPiecesCondition)
          .orderBy("dist".desc)
     )

    ZIO.serviceWithZIO[RestClickhouseDAO](_.executeSingleOptZIO(builder.sqlWithParameters().build, rowParser))
  }
}
