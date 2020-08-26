package databases

package clickhouse

import anorm.RowParser
import models.clickhouse._
import models.web.StatisticsParameters

abstract class StatisticsType

object AvgMax extends StatisticsType
object Accumulated extends StatisticsType
object OnlyRound extends StatisticsType

case class StatisticsCHRequest[T](aggregateSql: String, oneRoundSql: String, sortingColumns: Seq[(String, String)], statisticsType: StatisticsType, parser: RowParser[T]) {

  def execute(leagueId: Option[Int] = None,
              divisionLevel: Option[Int] = None,
              leagueUnitId: Option[Long] = None,
              teamId: Option[Long] = None,
              statisticsParameters: StatisticsParameters)(implicit clickhouseDAO: ClickhouseDAO) =
    clickhouseDAO.execute(request = this,
      leagueId = leagueId,
      season = Some(statisticsParameters.season),
      divisionLevel = divisionLevel,
      leagueUnitId = leagueUnitId,
      teamId = teamId,
      page = statisticsParameters.page,
      pageSize = statisticsParameters.pageSize,
      statsType = statisticsParameters.statsType,
      sortBy = statisticsParameters.sortBy,
      sortingDirection = statisticsParameters.sortingDirection)
}

object StatisticsCHRequest {
  val bestHatstatsTeamRequest = StatisticsCHRequest(
    aggregateSql = """select team_id,
                     |team_name,
                     |league_unit_id,
                     |league_unit_name,
                     |toInt32(__func__(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats,
                     |toInt32(__func__(rating_midfield)) as midfield,
                     |toInt32(__func__((rating_right_def + rating_left_def + rating_mid_def))) as defense,
                     |toInt32(__func__( (rating_right_att + rating_mid_att + rating_left_att))) as attack
                     |from hattrick.match_details __where__ and rating_midfield + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att != 0
                     |group by team_id, team_name, league_unit_id, league_unit_name order by __sortBy__ __sortingDirection__, team_id asc __limit__""".stripMargin,
    oneRoundSql = """select team_id,
                    |team_name,
                    |league_unit_id,
                    |league_unit_name,
                    |rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att as hatstats,
                    |rating_midfield as midfield,
                    |toInt32((rating_right_def + rating_left_def + rating_mid_def) ) as defense,
                    |toInt32( (rating_right_att + rating_mid_att + rating_left_att) ) as attack
                    |from hattrick.match_details __where__ and round = __round__
                    | order by __sortBy__ __sortingDirection__,  team_id asc __limit__""".stripMargin,
    sortingColumns = Seq(("hatstats", "table.hatstats"), ("midfield", "table.midfield"),
      ("defense", "table.defense"), ("attack", "table.attack")),
    statisticsType = AvgMax,
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
                     |     toInt32(avg((rating_right_def + rating_left_def + rating_mid_def))) as defense,
                     |     toInt32(avg((rating_right_att + rating_mid_att + rating_left_att))) as attack
                     |     from hattrick.match_details
                     |     __where__ and rating_midfield + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att != 0
                     |     group by league_unit_id, league_unit_name, round)
                     |group by league_unit_id, league_unit_name order by __sortBy__ __sortingDirection__, league_unit_id desc __limit__""".stripMargin,
    oneRoundSql = """select league_unit_id,
                    |     league_unit_name,
                    |     toInt32(avg(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats,
                    |     toInt32(avg(rating_midfield)) as midfield,
                    |     toInt32(avg((rating_right_def + rating_left_def + rating_mid_def))) as defense,
                    |     toInt32(avg((rating_right_att + rating_mid_att + rating_left_att))) as attack
                    |     from hattrick.match_details
                    |     __where__ and round = __round__ and rating_midfield + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att != 0
                    |     group by league_unit_id, league_unit_name
                    | order by __sortBy__ __sortingDirection__, league_unit_id __sortingDirection__ __limit__""".stripMargin,
    sortingColumns = Seq(("hatstats", "table.hatstats"), ("midfield", "table.midfield"),
      ("defense", "table.defense"), ("attack", "table.attack")),
    statisticsType = AvgMax,
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
                     |    max((age * 112) + days)  AS age,
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
                     |ORDER BY __sortBy__ __sortingDirection__, player_id __sortingDirection__
                     |__limit__""".stripMargin,
    oneRoundSql = """SELECT
                    |    player_id,
                    |    first_name,
                    |    last_name,
                    |    team_id,
                    |    team_name,
                    |    league_unit_id,
                    |    league_unit_name,
                    |    (age * 112) + days AS age,
                    |    if(played_minutes > 0, 1, 0) AS games,
                    |    played_minutes AS played,
                    |    goals AS scored,
                    |    yellow_cards AS yellow_cards,
                    |    red_cards AS red_cards,
                    |    if((played_minutes > 0) AND (injury_level > 0), injury_level, 0) AS total_injuries,
                    |    floor(played / scored, 2) AS goal_rate
                    |FROM hattrick.player_stats
                    |__where__ AND (round = __round__)
                    |ORDER BY __sortBy__ __sortingDirection__
                    |__limit__""".stripMargin,
    sortingColumns = Seq(("age", "table.age"), ("games", "table.games"), ("played", "table.minutes"),
      ("scored", "table.scored"), ("yellow_cards", "table.yellow_cards"), ("red_cards", "table.red_cards"),
      ("total_injuries", "table.injury"), ("goal_rate", "table.minutes_per_goal")),
    statisticsType = Accumulated,
    parser = PlayerStats.playerStatsMapper
  )

  val teamStateRequest = StatisticsCHRequest(
    aggregateSql = """""",
    oneRoundSql = """SELECT
                    |    team_name,
                    |    team_id,
                    |    league_unit_id,
                    |    league_unit_name,
                    |    sum(tsi) AS tsi,
                    |    sum(salary) AS salary,
                    |    sum(rating) AS rating,
                    |    sum(rating_end_of_match) AS rating_end_of_match,
                    |    toUInt32(avg((age * 112) + days)) AS age,
                    |    sumIf(injury_level, (played_minutes > 0) AND (injury_level > 0)) AS injury,
                    |    countIf(injury_level, (played_minutes > 0) AND (injury_level > 0)) AS injury_count
                    |FROM hattrick.player_stats
                    |__where__ AND (round = __round__)
                    |GROUP BY
                    |    team_name,
                    |    team_id,
                    |    league_unit_id,
                    |    league_unit_name
                    |ORDER BY __sortBy__ __sortingDirection__, team_id asc
                    |__limit__""".stripMargin,
    sortingColumns = Seq(("tsi", "table.tsi"), ("salary", "table.salary"), ("rating", "table.rating"),
      ("rating_end_of_match", "table.rating_end_of_match"), ("age", "table.average_age"),
      ("injury", "table.total_injury_weeks"), ("injury_count", "table.total_injury_number")),
    statisticsType = OnlyRound,
    parser = TeamState.teamStateMapper
  )

  val playerStateRequest = StatisticsCHRequest(
    aggregateSql = "",
    oneRoundSql = """SELECT
                    |    team_name,
                    |    team_id,
                    |    league_unit_name,
                    |    league_unit_id,
                    |    player_id,
                    |    first_name,
                    |    last_name,
                    |    ((age * 112) + days)  AS age,
                    |    tsi,
                    |    salary,
                    |    rating,
                    |    rating_end_of_match,
                    |    injury_level,
                    |    red_cards,
                    |    yellow_cards
                    |FROM hattrick.player_stats
                    |__where__ AND (round = __round__)
                    |ORDER BY
                    |    __sortBy__ __sortingDirection__, player_id __sortingDirection__
                    |__limit__""".stripMargin,
    sortingColumns = Seq(("age", "table.age"), ("tsi", "table.tsi"), ("salary", "table.salary"),
      ("rating", "table.rating"), ("rating_end_of_match", "table.rating_end_of_match"),
      ("injury_level", "table.injury"), ("red_cards", "table.red_cards"), ("yellow_cards", "table.yellow_cards")),
    statisticsType = OnlyRound,
    parser = PlayersState.playersStateMapper
  )

  val formalTeamStats = StatisticsCHRequest(
    aggregateSql = "",
    oneRoundSql = """SELECT
                    |    team_id,
                    |    team_name,
                    |    league_unit_id,
                    |    league_unit_name,
                    |    sum(goals) AS scored,
                    |    sum(enemy_goals) AS missed,
                    |    countIf(goals > enemy_goals) AS wins,
                    |    countIf(goals = enemy_goals) AS draws,
                    |    countIf(goals < enemy_goals) AS loses,
                    |    (3 * wins) + draws AS points
                    |FROM hattrick.match_details
                    |__where__ and round <= __round__
                    |GROUP BY
                    |    team_name,
                    |    team_id,
                    |    league_unit_id,
                    |    league_unit_name
                    |ORDER BY
                    |    __sortBy__ __sortingDirection__,
                    |    scored __sortingDirection__,
                    |    team_id __sortingDirection__
                    |__limit__""".stripMargin,
    sortingColumns = Seq(("scored", "table.scored"), ("missed", "table.goals_against"), ("wins", "table.win"),
      ("draws", "table.draw"), ("loses", "table.lose"), ("points", "table.points")),
    statisticsType = OnlyRound,
    parser = FormalTeamStats.formalTeamStatsMapper
  )
}