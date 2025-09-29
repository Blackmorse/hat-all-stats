package databases.requests.playerstats.dreamteam

import anorm.{Row, RowParser, SimpleSql}
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.*
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.player.DreamTeamPlayer
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import models.web.{Accumulate, Round, SqlInjectionError, StatsType}
import sqlbuilder.{Field, NestedSelect, Select}
import zio.ZIO

object DreamTeamRequest extends ClickhouseRequest[DreamTeamPlayer] {
  override val rowParser: RowParser[DreamTeamPlayer] = DreamTeamPlayer.mapper

  private def simpleSql(orderingKeyPath: OrderingKeyPath, statsType: StatsType, sortBy: String): SimpleSql[Row] = {
    import sqlbuilder.SqlBuilder.implicits.*
    val fields: Seq[Field] = Seq("league_id",
      "player_id",
      "first_name",
      "last_name",
      "team_id",
      "team_name",
      "league_unit_id",
      "league_unit_name",
      "round",
      ClickhouseRequest.roleIdCase("role_id") `as` "role",
      "rating",
      "rating_end_of_match",
      "nationality")

    val sortings = if (sortBy == "rating")
      Seq("rating".desc, "rating_end_of_match".desc)
    else
      Seq("rating_end_of_match".desc, "rating".desc)

    val builder = statsType match {
      case Accumulate =>
        Select("*").from(
          NestedSelect(fields*).from("hattrick.player_stats")
            .where
            .orderingKeyPath(orderingKeyPath)
            .season(orderingKeyPath.season)
            .isLeagueMatch
            .and("role_id != 0")
            .orderBy(sortings*)
            .limitBy(1, "role, player_id")
        ).limitBy(4, "role")
      case Round(r) =>
        Select(fields*)
          .from("hattrick.player_stats")
          .where
          .orderingKeyPath(orderingKeyPath)
          .season(orderingKeyPath.season)
          .isLeagueMatch
          .round(r)
          .and("role_id != 0")
          .orderBy(sortings*)
          .limitBy(4, "role")
    }
    
    builder.sqlWithParameters().build
  }
  
  def execute(orderingKeyPath: OrderingKeyPath, statsType: StatsType, sortBy: String)
                : DBIO[List[DreamTeamPlayer]] = wrapErrors {
    if(!Seq("rating", "rating_end_of_match").contains(sortBy)) {
      ZIO.fail(SqlInjectionError())
    } else {
      for {
        restClickhouseDAO <- ZIO.service[RestClickhouseDAO]
        result <- restClickhouseDAO.executeZIO(simpleSql(orderingKeyPath, statsType, sortBy), rowParser)
      } yield result
    }
  }
}
