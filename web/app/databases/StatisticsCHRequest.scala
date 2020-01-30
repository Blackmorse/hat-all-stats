package databases

package clickhouse

import anorm.RowParser
import models.clickhouse.{LeagueUnitRating, PlayerStats, TeamRating}
import models.web.StatsType

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
    oneRoundSql = """select league_unit_id,
                    |     league_unit_name,
                    |     toInt32(avg(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats,
                    |     toInt32(avg(rating_midfield)) as midfield,
                    |     toInt32(avg((rating_right_def + rating_left_def + rating_mid_def) / 3)) as defense,
                    |     toInt32(avg((rating_right_att + rating_mid_att + rating_left_att) / 3)) as attack
                    |     from hattrick.match_details
                    |     __where__ and round = __round__ and rating_midfield + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att != 0
                    |     group by league_unit_id, league_unit_name
                    | order by __sortBy__ desc, league_unit_id desc __limit__""".stripMargin,
    sortingColumns = Seq("hatstats", "midfield", "defense", "attack"),
    parser = LeagueUnitRating.leagueUnitRatingMapper
  )

  val playerStatsRequest = StatisticsCHRequest(
    aggregateSql = """SELECT
                     |    player_id,
                     |    first_name,
                     |    last_name,
                     |    team_id,
                     |    team_name,
                     |    league_unit_id,
                     |    league_unit_name,
                     |    floor(max((age * 112) + days) / 112, 1) AS age,
                     |    countIf(played_minutes > 0) AS games,
                     |    sum(played_minutes) AS played,
                     |    sum(goals) AS scored,
                     |    sum(yellow_cards) AS yellow_cards,
                     |    sum(red_cards) AS red_cards,
                     |    sumIf(injury_level, (played_minutes > 0) AND (injury_level > 0)) AS total_injuries,
                     |    floor(played / scored, 2) AS goal_rate
                     |FROM hattrick.player_stats
                     |__where__
                     |GROUP BY
                     |    player_id,
                     |    first_name,
                     |    last_name,
                     |    team_id,
                     |    team_name,
                     |    league_unit_id,
                     |    league_unit_name
                     |ORDER BY __sortBy__ DESC, player_id DESC
                     |__limit__""".stripMargin,
    oneRoundSql = """SELECT
                    |    player_id,
                    |    first_name,
                    |    last_name,
                    |    team_id,
                    |    team_name,
                    |    league_unit_id,
                    |    league_unit_name,
                    |    floor(((age * 112) + days) / 112, 1) AS age,
                    |    if(played_minutes > 0, 1, 0) AS games,
                    |    played_minutes AS played,
                    |    goals AS scored,
                    |    yellow_cards AS yellow_cards,
                    |    red_cards AS red_cards,
                    |    if((played_minutes > 0) AND (injury_level > 0), injury_level, 0) AS total_injuries,
                    |    floor(played / scored, 2) AS goal_rate
                    |FROM hattrick.player_stats
                    |__where__ AND (round = __round__)
                    |ORDER BY __sortBy__ DESC
                    |__limit__""".stripMargin,
    sortingColumns = Seq("age", "games", "played", "scored", "yellow_cards", "red_cards", "total_injuries"),//TODO goal_rate
    parser = PlayerStats.playerStatsMapper
  )

}

