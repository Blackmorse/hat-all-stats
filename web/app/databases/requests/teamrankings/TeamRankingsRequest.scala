package databases.requests.teamrankings

import anorm.{Row, RowParser, SimpleSql}
import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import models.clickhouse.TeamRankings
import sqlbuilder.{Select, SqlBuilder}
import ClickhouseRequest.*
import models.web.HattidError
import zio.{IO, ZIO}

object TeamRankingsRequest extends ClickhouseRequest[TeamRankings]{

  import SqlBuilder.implicits._
  def select: SqlBuilder = Select(
      "team_id",
      "team_name",
      "division_level",
      "season",
      "round",
      "rank_type",
      "match_id",
      "hatstats",
      "hatstats_position",
      "attack",
      "attack_position",
      "midfield",
      "midfield_position",
      "defense",
      "defense_position",
      "loddar_stats",
      "loddar_stats_position",
      "tsi",
      "tsi_position",
      "salary",
      "salary_position",
      "rating",
      "rating_position",
      "rating_end_of_match",
      "rating_end_of_match_position",
      "age".toInt32 `as` "age",
      "age_position",
      "injury",
      "injury_position",
      "injury_count",
      "injury_count_position",
      "power_rating",
      "power_rating_position",
      "founded",
      "founded_position"
    ).from("hattrick.team_rankings")

  override val rowParser: RowParser[TeamRankings] = TeamRankings.teamRankingsMapper

  private def simpleSql(fromSeason: Option[Int], toSeason: Option[Int], leagueId: Int, teamId: Long): SimpleSql[Row] = {
      select
        .where
        .season.greaterEqual(fromSeason)
        .season.lessEqual(toSeason)
        .leagueId(leagueId)
        .teamId(teamId)
        .orderBy(
            "rank_type".asc,
            "round".asc
        )
        .sqlWithParameters().build
  }

  def execute(fromSeason: Option[Int], toSeason: Option[Int], leagueId: Int, teamId: Long)
             : DBIO[List[TeamRankings]] = wrapErrors {
    for {
      restClickhouseDAO <- ZIO.service[RestClickhouseDAO]
      result <- restClickhouseDAO.executeZIO(simpleSql(fromSeason, toSeason, leagueId, teamId), rowParser)
    } yield result
  }
}
