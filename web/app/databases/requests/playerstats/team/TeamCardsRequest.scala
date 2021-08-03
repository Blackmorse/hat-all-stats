package databases.requests.playerstats.team

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.team.TeamCards
import databases.sqlbuilder.{Select, SqlBuilder}
import models.web.{RestStatisticsParameters, Round}

import scala.concurrent.Future

object TeamCardsRequest extends ClickhouseRequest[TeamCards] {
  val sortingColumns: Seq[String] = Seq("yellow_cards", "red_cards")

  override val rowParser: RowParser[TeamCards] = TeamCards.mapper

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamCards]] = {
    val sortBy = parameters.sortBy
    if(!sortingColumns.contains(sortBy))
      throw new Exception("Looks like SQL injection")

    val round = parameters.statsType match {
      case Round(r) => r
    }

    import SqlBuilder.implicits._
    val builder = Select(
        "any(league_id)" as "league",
        "argMax(team_name, round)" as "team_name",
        "team_id",
        "league_unit_id",
        "league_unit_name",
        "sum(yellow_cards)" as "yellow_cards",
        "sum(red_cards)" as "red_cards"
      ).from("hattrick.player_stats")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .round.lessEqual(round)
      .groupBy("team_id", "league_unit_id", "league_unit_name")
      .orderBy(
        sortBy.to(parameters.sortingDirection),
        "team_id".asc
      ).limit(page = parameters.page, pageSize = parameters.pageSize)

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
