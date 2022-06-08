package databases.requests.playerstats.dreamteam

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.implicits.{ClauseEntryExtended, SqlWithParametersExtended}
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.player.DreamTeamPlayer
import models.web.{Accumulate, Round, StatsType}
import sqlbuilder.{Field, NestedSelect, Select}

import scala.concurrent.Future

object DreamTeamRequest extends ClickhouseRequest[DreamTeamPlayer] {
  override val rowParser: RowParser[DreamTeamPlayer] = DreamTeamPlayer.mapper

  def execute(orderingKeyPath: OrderingKeyPath, statsType: StatsType, sortBy: String)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[DreamTeamPlayer]] = {
    if(!Seq("rating", "rating_end_of_match").contains(sortBy)) {
      throw new Exception("Unknown sorting field")
    }

    import sqlbuilder.SqlBuilder.implicits._
    val fields: Seq[Field] = Seq("league_id",
      "player_id",
      "first_name",
      "last_name",
      "team_id",
      "team_name",
      "league_unit_id",
      "league_unit_name",
      "round",
      ClickhouseRequest.roleIdCase("role_id") as "role",
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
          NestedSelect(fields: _*).from("hattrick.player_stats")
            .where
              .orderingKeyPath(orderingKeyPath)
              .season(orderingKeyPath.season)
              .isLeagueMatch
              .and("role_id != 0")
            .orderBy(sortings: _*)
            .limitBy(1, "role, player_id")
        ).limitBy(4, "role")
      case Round(r) =>
        Select(fields: _*)
          .from("hattrick.player_stats")
          .where
            .orderingKeyPath(orderingKeyPath)
            .season(orderingKeyPath.season)
            .isLeagueMatch
            .round(r)
            .and("role_id != 0")
          .orderBy(sortings: _*)
          .limitBy(4, "role")
    }

    restClickhouseDAO.execute(builder.sqlWithParameters().build, rowParser)
  }
}
