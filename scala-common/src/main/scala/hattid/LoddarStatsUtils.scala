package hattid

object LoddarStatsUtils {
  lazy val homeLoddarStats: String = loddarStats("")

  lazy val awayLoddarStats: String = loddarStats("opposite_")

  private def loddarStats(prefix: String): String =
    s"""
       |multiIf(${prefix}tactic_type = 2, ${ca(prefix)},
       |        ${prefix}tactic_type = 4, ${attackWings(prefix)},
       |        ${prefix}tactic_type = 3, ${attackMiddle(prefix)},
       |        ${noTactic(prefix)})
       |""".stripMargin


  private def ca(prefix: String): String = {
    s"""
       |80 * ${hq("rating_midfield", prefix)} *
       |			(
       |				(0.47 * ((0.37 * ${hq("rating_mid_def", prefix)})	+ (0.315 * (${hq("rating_left_def", prefix)} + ${hq("rating_right_def", prefix)}))))
       |					+
       |				((0.53 + ((0.5 * $prefix${"tactic_skill"}) / ($prefix${"tactic_skill"} + 20))) * ((0.37 * ${hq("rating_mid_att", prefix)}) + (0.315 * (${hq("rating_left_att", prefix)} + ${hq("rating_right_att", prefix)}))))
       |			)
       |""".stripMargin
  }

  private def attackWings(prefix: String): String = {
    s"""
       |80 * ${hq("rating_midfield", prefix)} *
       |			(
       |				( 0.47 * ((0.37 * ${hq("rating_mid_def", prefix)}) + (0.315 * (${hq("rating_left_def", prefix)} + ${hq("rating_right_def", prefix)}))))
       |					+
       |				(0.53 * (((0.37 - (((0.2 * ($prefix${"tactic_skill"} - 1)) / 19) + 0.2)) * ${hq("rating_mid_att", prefix)})
       |					+ (((0.63 + (((0.2 * ($prefix${"tactic_skill"} - 1)) / 19) + 0.2)) / 2) * (${hq("rating_left_att", prefix)} + ${hq("rating_right_att", prefix)}))))
       |			)
       |""".stripMargin
  }

  private def attackMiddle(prefix: String): String = {
    s"""
       |80 * ${hq("rating_midfield", prefix)} * (
       |			(0.47 * ((0.37 * ${hq("rating_mid_def", prefix)}) + (0.315 * (${hq("rating_left_def", prefix)} + ${hq("rating_right_def", prefix)}))))
       |				+
       |			0.53 * (((0.37 + (((0.2 * ($prefix${"tactic_skill"} - 1)) / 19) + 0.2)) * ${hq("rating_mid_att", prefix)})
       |				+ (((0.63 - (((0.2 * ($prefix${"tactic_skill"} - 1)) / 19) + 0.2)) / 2) * (${hq("rating_left_att", prefix)} + ${hq("rating_right_att", prefix)}))))
       |""".stripMargin
  }

  private def noTactic(prefix: String): String = {
    s"""
       |80 * ${hq("rating_midfield", prefix)} *
       |(
       |  0.47 * ((0.37 * ${hq("rating_mid_def", prefix)}) + (0.315 * (${hq("rating_left_def", prefix)} + ${hq("rating_right_def", prefix)}))) +
       |     0.53 * ((0.37 * ${hq("rating_mid_att", prefix)}) + (0.315 * (${hq("rating_left_att", prefix)} + ${hq("rating_right_att", prefix)})))
       |)
       |""".stripMargin
  }

  private def hq(field: String, prefix: String): String = {
    val x = s"((((($prefix$field + 3) / 4) - 1) * 4) + 1)"
    s"((2 * $x) / ($x + 80))"
  }
}
