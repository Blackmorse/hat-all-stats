package com.blackmorse.hattid.web.databases.sql

import hattid.LoddarStatsUtils
import sqlbuilder.{Field, SqlBuilder}

object Fields {
  import SqlBuilder.implicits._

  val hatstats: Field = "rating_midfield * 3 + rating_left_att + rating_right_att + rating_mid_att + rating_left_def + rating_right_def + rating_mid_def"
  val oppositeHatstats: Field = "opposite_rating_midfield * 3 + opposite_rating_left_att + opposite_rating_right_att + opposite_rating_mid_att + opposite_rating_left_def + opposite_rating_right_def + opposite_rating_mid_def"

  val loddarStats: Field = LoddarStatsUtils.homeLoddarStats
  val oppositeLoddarStats: Field = LoddarStatsUtils.awayLoddarStats
}
