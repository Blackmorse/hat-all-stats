package databases.requests.playerstats.team

import anorm.RowParser
import databases.SqlBuilder
import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.team.TeamCards
import models.web.{RestStatisticsParameters, Round}

import scala.concurrent.Future

object TeamCardsRequest extends ClickhouseRequest[TeamCards] {
  val sortingColumns: Seq[String] = Seq("yellow_cards", "red_cards")

  override val rowParser: RowParser[TeamCards] = TeamCards.mapper

  val oneRoundSql: String =
    """
      |SELECT
      |    any(league_id) as league,
      |    argMax(team_name, round) as team_name,
      |    team_id,
      |    league_unit_id,
      |    league_unit_name,
      |    sum(yellow_cards) as yellow_cards,
      |    sum(red_cards) as red_cards
      |FROM hattrick.player_stats
      |__where__
      |GROUP BY
      |    team_id,
      |    league_unit_id,
      |    league_unit_name
      |ORDER BY __sortBy__ __sortingDirection__, team_id asc
      |__limit__
      |""".stripMargin

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamCards]] = {
    val sortBy = parameters.sortBy
    if(!sortingColumns.contains(sortBy))
      throw new Exception("Looks like SQL injection")

    val round = parameters.statsType match {
      case Round(r) => r
    }

    restClickhouseDAO.execute(SqlBuilder(oneRoundSql)
      .where
        .applyParameters(parameters)
        .applyParameters(orderingKeyPath)
        .round.lessEqual(round)
      .sortBy(sortBy)
      .build, rowParser)
  }
}
