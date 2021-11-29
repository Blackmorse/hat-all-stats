package databases.requests.matchdetails

import anorm.RowParser
import databases.requests.model.team.TeamHatstats
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import databases.sqlbuilder.{Select, SqlBuilder}
import models.web.RestStatisticsParameters

object TeamHatstatsRequest extends ClickhouseStatisticsRequest[TeamHatstats]{
  override val sortingColumns: Seq[String] = Seq("hatstats", "midfield", "defense", "attack", "loddar_stats")
  override val rowParser: RowParser[TeamHatstats] = TeamHatstats.teamRatingMapper.asInstanceOf[RowParser[TeamHatstats]]

  override def oneRoundBuilder(orderingKeyPath: OrderingKeyPath,
                               parameters: RestStatisticsParameters,
                               round: Int): SqlBuilder = {
    import SqlBuilder.fields._
    import SqlBuilder.implicits._
    Select("team_id",
        "league_id" as "league",
        "team_name",
        "league_unit_id",
        "league_unit_name",
        hatstats as "hatstats",
        "rating_midfield" as "midfield",
        "toInt32(rating_right_def + rating_left_def + rating_mid_def)" as "defense",
        "toInt32(rating_right_att + rating_mid_att + rating_left_att)" as "attack",
        loddarStats as "loddar_stats"
      )
      .from("hattrick.match_details")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .round(round)
        .isLeagueMatch
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection),
        "team_id".asc)
      .limit(page = parameters.page, pageSize = parameters.pageSize)

  }

  override def aggregateBuilder(orderingKeyPath: OrderingKeyPath,
                                parameters: RestStatisticsParameters,
                                aggregateFunction: SqlBuilder.func): SqlBuilder = {
    import SqlBuilder.fields._
    import SqlBuilder.implicits._
    Select(
        "team_id",
        "any(league_id)" as "league",
        "argMax(team_name, round) as team_name",
        "league_unit_id",
        "league_unit_name",
        aggregateFunction(hatstats).toInt32 as "hatstats",
        aggregateFunction("rating_midfield").toInt32 as " midfield",
        aggregateFunction("rating_right_def + rating_left_def + rating_mid_def").toInt32 as "defense",
        aggregateFunction("rating_right_att + rating_mid_att + rating_left_att").toInt32 as "attack",
        aggregateFunction(loddarStats) as "loddar_stats"
      )
      .from("hattrick.match_details")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .isLeagueMatch
        .and(s"rating_midfield * 3 + rating_left_att + rating_right_att + rating_mid_att + rating_left_def + rating_right_def + rating_mid_def + " +
          s"opposite_rating_midfield * 3 + opposite_rating_left_att + opposite_rating_right_att + opposite_rating_mid_att + opposite_rating_left_def + opposite_rating_right_def + opposite_rating_mid_def != 0")
      .groupBy(
        "team_id",
        "league_unit_id",
        "league_unit_name")
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection),
        "team_id".asc)
      .limit(page = parameters.page, pageSize = parameters.pageSize)
  }
}
