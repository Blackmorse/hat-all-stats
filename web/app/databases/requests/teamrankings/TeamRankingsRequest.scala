package databases.requests.teamrankings

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.sqlbuilder.SqlBuilder
import models.clickhouse.TeamRankings
import models.web.Asc

import scala.concurrent.Future

object TeamRankingsRequest extends ClickhouseRequest[TeamRankings]{
  val request: String = """
    |SELECT
       |    team_id,
       |    team_name,
       |    division_level,
       |    season,
       |    round,
       |    rank_type,
       |    match_id,
       |    hatstats,
       |    hatstats_position,
       |    attack,
       |    attack_position,
       |    midfield,
       |    midfield_position,
       |    defense,
       |    defense_position,
       |    loddar_stats,
       |    loddar_stats_position,
       |    tsi,
       |    tsi_position,
       |    salary,
       |    salary_position,
       |    rating,
       |    rating_position,
       |    rating_end_of_match,
       |    rating_end_of_match_position,
       |    toInt32(age) as age,
       |    age_position,
       |    injury,
       |    injury_position,
       |    injury_count,
       |    injury_count_position,
       |    power_rating,
       |    power_rating_position
       |FROM hattrick.team_rankings
       | __where__
       | __orderBy__
""".stripMargin

  override val rowParser: RowParser[TeamRankings] = TeamRankings.teamRankingsMapper

  def execute(orderingKeyPath: OrderingKeyPath)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamRankings]] =
    restClickhouseDAO.execute(SqlBuilder(request)
      .where
        .season(orderingKeyPath.season)
        .leagueId(orderingKeyPath.leagueId)
        .teamId(orderingKeyPath.teamId)
      .orderBy("rank_type", "round")
      .sortingDirection(Asc)
      .build, rowParser)
}
