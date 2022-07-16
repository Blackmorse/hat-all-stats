package databases.requests.playerstats.player

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest
import databases.requests.model.player.PlayerHistory
import sqlbuilder.Select

import scala.concurrent.Future

object PlayerHistoryRequest extends ClickhouseRequest[PlayerHistory] {
  override val rowParser: RowParser[PlayerHistory] = PlayerHistory.mapper

  def execute(playerId: Long)(implicit restClickhouseDAO: RestClickhouseDAO): Future[List[PlayerHistory]] = {
    import sqlbuilder.SqlBuilder.implicits._

    val builder = Select(
      "league_id",
      "league_unit_id",
      "league_unit_name",
      "player_id",
      "first_name",
      "last_name",
      "team_id",
      "team_name",
      "(age * 112) + days" as "age",
      "tsi",
      "rating",
      "rating_end_of_match",
      "cup_level",
      ClickhouseRequest.roleIdCase("role_id") as "role",
      "played_minutes",
      "injury_level",
      "salary",
      "yellow_cards",
      "red_cards",
      "goals",
      "season",
      "round",
      "nationality"
    ).from("hattrick.player_stats")
      .where
      //TODO: ability to add typed parameters with custom name
      .and(s"player_id = $playerId")
      .orderBy("season", "round", "cup_level".asc)

    restClickhouseDAO.execute(builder.sqlWithParameters().build, rowParser)
  }
}
