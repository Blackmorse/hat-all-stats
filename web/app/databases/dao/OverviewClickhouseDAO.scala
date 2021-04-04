package databases.dao

import anorm.ResultSetParser
import databases.requests.model.overview._
import databases.sqlbuilder.SqlBuilder
import play.api.db.DBApi

import javax.inject.{Inject, Singleton}

@Singleton
class OverviewClickhouseDAO @Inject()(
                                      dbApi: DBApi)
                                     (implicit ec: DatabaseExecutionContext){
  private val db = dbApi.database("default")

  def numberOfTeams(round: Int, season: Int, leagueId: Option[Int]) =
    stats("SELECT count() AS count FROM hattrick.match_details __where__",
      round, season, leagueId, CountsModel.mapper.single)

  def overviewPlayerState(round: Int, season: Int, leagueId: Option[Int]) =
    stats("SELECT count() as count, countIf(injury_level > 0) AS injuried, sum(goals) as goals, " +
      "sum(yellow_cards) as yellow_cards, sum(red_cards) as red_cards " +
      " FROM hattrick.player_stats __where__",
      round, season, leagueId, OverviewPlayerStats.mapper.single)

  def topSalaryTeams(round: Int, season: Int, leagueId: Option[Int]) =
    stats("""SELECT any(season) as team_season, any(round) as team_round, league_id, league_unit_id, league_unit_name, team_name, team_id, sum(salary) AS value
                 |FROM hattrick.player_stats __where__
                 |GROUP BY  league_id, league_unit_id, league_unit_name, team_id, team_name
                 |ORDER BY value DESC
                 |LIMIT 5
                 |""".stripMargin, round, season, leagueId, TeamOverviewModel.mapper.*)

  def topHatstatsTeams(round: Int, season: Int, leagueId: Option[Int]) =
    stats("""SELECT season as team_season, round as team_round, league_id, league_unit_id, league_unit_name, team_id, team_name,
                 | rating_midfield * 3 + rating_left_def + rating_mid_def + rating_right_def + rating_left_att + rating_right_att + rating_mid_att AS value
                 | FROM hattrick.match_details __where__ ORDER BY value DESC LIMIT 5
                 |""".stripMargin, round, season, leagueId, TeamOverviewModel.mapper.*)

  def topHatstatsMatches(round: Int, season: Int, leagueId: Option[Int]) =
    stats("""SELECT league_id, league_unit_id, league_unit_name, team_id, team_name,
                 | opposite_team_id, opposite_team_name, goals, enemy_goals, match_id, is_home_match, dt,
                 |    rating_midfield * 3 + rating_left_att + rating_mid_att + rating_right_att + rating_left_def + rating_right_def + rating_mid_def AS value,
                 |    opposite_rating_midfield * 3 + opposite_rating_left_att + opposite_rating_right_att + opposite_rating_mid_att + opposite_rating_left_def + opposite_rating_right_def + opposite_rating_mid_def AS opposite_value
                 |FROM hattrick.match_details  __where__
                 |ORDER BY value + opposite_value DESC
                 |LIMIT 1 BY match_id LIMIT 5
                 |""".stripMargin, round, season, leagueId, MatchOverviewModel.mapper.*)

  def topRandomMatches(round: Int, season: Int, leagueId: Option[Int]) =
    stats("""SELECT league_id, league_unit_id, league_unit_name, team_id, team_name, opposite_team_id, opposite_team_name,
                 | goals, enemy_goals, match_id, is_home_match, dt,
                 | rating_midfield * 3 + rating_left_att + rating_mid_att + rating_right_att + rating_left_def + rating_right_def + rating_mid_def AS value,
                 | opposite_rating_midfield * 3 + opposite_rating_left_att + opposite_rating_right_att + opposite_rating_mid_att + opposite_rating_left_def + opposite_rating_right_def + opposite_rating_mid_def AS opposite_value
                 |FROM hattrick.match_details
                 | __where__ AND (((goals - enemy_goals) * (value - opposite_value)) < 0)
                 |ORDER BY abs(value - opposite_value) DESC
                 |LIMIT 1 BY match_id LIMIT 5
                 |""".stripMargin, round, season, leagueId, MatchOverviewModel.mapper.*)

  def avgMatchDetails(round: Int, season: Int, leagueId: Option[Int]) =
    stats("""SELECT
            |    toUInt32(avgIf(sold_total, is_home_match = 'home')) AS avg_sold_total,
            |    toUInt16(avg(rating_midfield + rating_left_att + rating_right_att + rating_mid_att + rating_left_def + rating_right_def + rating_mid_def)) AS avg_hatstats,
            |    avg(goals) AS avg_goals
            |FROM hattrick.match_details
            |__where__
            |""".stripMargin, round, season, leagueId, AvgMatchDetailsModel.mapper.single)

  def avgTeamPlayers(round: Int, season: Int, leagueId: Option[Int]) =
    stats("""SELECT
            |    toUInt16(avg(avg_age)) AS avg_age,
            |    toUInt32(avg(sum_salary)) AS avg_salary,
            |    avg(sum_rating) AS avg_rating
            |FROM
            |(
            |    SELECT
            |        avg((age * 112) + days) AS avg_age,
            |        sum(rating) AS sum_rating,
            |        sum(salary) AS sum_salary
            |    FROM hattrick.player_stats
            |    __where__
            |    GROUP BY team_id
            |)
            |""".stripMargin, round, season, leagueId, AvgTeamPlayersStats.mapper.single)

  def formations(round: Int, season: Int, leagueId: Option[Int]) =
    stats("""SELECT formation, count() AS count
            |FROM hattrick.match_details __where__
            |GROUP BY formation  ORDER BY count DESC
            |LIMIT 6""".stripMargin, round, season, leagueId, FormationsModel.mapper.*)

  def topSalaryPlayers(round: Int, season: Int, leagueId: Option[Int]) =
    stats("""SELECT season, round, league_id, league_unit_id, league_unit_name, team_id,
            | team_name, player_id, first_name, last_name, salary AS value
            |FROM hattrick.player_stats __where__
            |ORDER BY value DESC LIMIT 5
            |""".stripMargin, round, season, leagueId, PlayerOverviewModel.mapper.*)

  def topRatingPlayers(round: Int, season: Int, leagueId: Option[Int]) =
    stats("""SELECT season, round, league_id, league_unit_id, league_unit_name, team_id,
            | team_name, player_id, first_name, last_name, rating AS value
            |FROM hattrick.player_stats __where__
            |ORDER BY value DESC LIMIT 5
            |""".stripMargin, round, season, leagueId, PlayerOverviewModel.mapper.*)

  private def stats[T](sql: String, round: Int, season: Int, leagueId: Option[Int], parser: ResultSetParser[T]): T = db.withConnection{ implicit connection =>
    SqlBuilder(sql)
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
      .build.as(parser)
  }
}
