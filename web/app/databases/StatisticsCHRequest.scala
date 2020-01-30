package databases

package clickhouse

import anorm.RowParser
import models.clickhouse.{LeagueUnitRating, TeamRating}
import models.web.StatsType
import service.LeagueUnitTeamStat

case class StatisticsCHRequest[T](aggregateSql: String, oneRoundSql: String, sortingColumns: Seq[String], parser: RowParser[T]) {

  def execute(leagueId: Option[Int] = None,
              season: Option[Int] = None,
              divisionLevel: Option[Int] = None,
              leagueUnitId: Option[Long] = None,
              page: Int = 0,
              statsType: StatsType,
              sortBy: String)(implicit clickhouseDAO: ClickhouseDAO) =
    clickhouseDAO.execute(this, leagueId, season, divisionLevel, leagueUnitId, page, statsType, sortBy)
}

object StatisticsCHRequest {
  val bestHatstatsTeamRequest = StatisticsCHRequest(
    aggregateSql = """select team_id,
                     |team_name,
                     |league_unit_id,
                     |league_unit_name,
                     |toInt32(__func__(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats,
                     |toInt32(__func__(rating_midfield)) as midfield,
                     |toInt32(__func__((rating_right_def + rating_left_def + rating_mid_def) / 3)) as defense,
                     |toInt32(__func__( (rating_right_att + rating_mid_att + rating_left_att) / 3)) as attack
                     |from hattrick.match_details __where__ and rating_midfield + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att != 0
                     |group by team_id, team_name, league_unit_id, league_unit_name order by __sortBy__ desc, team_id desc __limit__""".stripMargin,
    oneRoundSql = """select team_id,
                    |team_name,
                    |league_unit_id,
                    |league_unit_name,
                    |rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att as hatstats,
                    |rating_midfield as midfield,
                    |toInt32((rating_right_def + rating_left_def + rating_mid_def) / 3) as defense,
                    |toInt32( (rating_right_att + rating_mid_att + rating_left_att) / 3) as attack
                    |from hattrick.match_details __where__ and round = __round__
                    | order by __sortBy__ desc, team_id desc __limit__""".stripMargin,
    sortingColumns = Seq("hatstats", "midfield", "defense", "attack"),
    parser = TeamRating.teamRatingMapper
  )

  val bestHatstatsLeagueRequest = StatisticsCHRequest(
    aggregateSql = """select league_unit_id,
                     |league_unit_name,
                     |toInt32(__func__(hatstats)) as hatstats,
                     |toInt32(__func__(midfield)) as midfield,
                     |toInt32(__func__(defense)) as defense,
                     |toInt32(__func__(attack)) as attack
                     | from
                     |   (select league_unit_id,
                     |     league_unit_name,
                     |     round,
                     |     toInt32(avg(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats,
                     |     toInt32(avg(rating_midfield)) as midfield,
                     |     toInt32(avg((rating_right_def + rating_left_def + rating_mid_def) / 3)) as defense,
                     |     toInt32(avg((rating_right_att + rating_mid_att + rating_left_att) / 3)) as attack
                     |     from hattrick.match_details
                     |     __where__ and rating_midfield + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att != 0
                     |     group by league_unit_id, league_unit_name, round)
                     |group by league_unit_id, league_unit_name order by __sortBy__ desc, league_unit_id desc __limit__""".stripMargin,
    oneRoundSql = "",
    sortingColumns = Seq("hatstats", "midfield", "defense", "attack"),
    parser = LeagueUnitRating.leagueUnitRatingMapper
  )
}

