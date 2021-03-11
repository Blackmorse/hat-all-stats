package databases.requests.matchdetails

import anorm.RowParser
import databases.requests.ClickhouseStatisticsRequest
import databases.requests.model.team.TeamHatstats

object TeamHatstatsRequest extends ClickhouseStatisticsRequest[TeamHatstats]{
  override val aggregateSql: String =
    """select team_id,
          |any(league_id) as league,
          |argMax(team_name, round) as team_name,
          |league_unit_id,
          |league_unit_name,
          |toInt32(__func__(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats,
          |toInt32(__func__(rating_midfield)) as midfield,
          |toInt32(__func__((rating_right_def + rating_left_def + rating_mid_def))) as defense,
          |toInt32(__func__( (rating_right_att + rating_mid_att + rating_left_att))) as attack
      |from hattrick.match_details __where__ and rating_midfield + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att != 0
      |group by team_id, league_unit_id, league_unit_name order by __sortBy__ __sortingDirection__, team_id asc __limit__""".stripMargin

  override val oneRoundSql: String =
    """select team_id,
          |league_id as league,
          |team_name,
          |league_unit_id,
          |league_unit_name,
          |rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att as hatstats,
          |rating_midfield as midfield,
          |toInt32((rating_right_def + rating_left_def + rating_mid_def) ) as defense,
          |toInt32( (rating_right_att + rating_mid_att + rating_left_att) ) as attack
      |from hattrick.match_details __where__ AND round = __round__
      | order by __sortBy__ __sortingDirection__,  team_id asc __limit__
      |""".stripMargin

  override val sortingColumns: Seq[String] = Seq("hatstats", "midfield", "defense", "attack")
  override val rowParser: RowParser[TeamHatstats] = TeamHatstats.teamRatingMapper.asInstanceOf[RowParser[TeamHatstats]]
}
