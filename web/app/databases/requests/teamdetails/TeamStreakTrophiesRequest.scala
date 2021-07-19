package databases.requests.teamdetails

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import databases.requests.model.team.TeamStreakTrophies
import databases.sqlbuilder.SqlBuilder
import models.web.{RestStatisticsParameters, Round}

import scala.concurrent.Future

object TeamStreakTrophiesRequest extends ClickhouseStatisticsRequest[TeamStreakTrophies] {
  override val sortingColumns: Seq[String] = Seq("trophies_number", "number_of_victories", "number_of_undefeated")

  override val aggregateSql: String = ""

  override val oneRoundSql: String = """
     |SELECT
     |    league_id,
     |    team_id,
     |    team_name,
     |    league_unit_id,
     |    league_unit_name,
     |    trophies_number,
     |    number_of_victories,
     |    number_of_undefeated
     |FROM hattrick.team_details
     | __where__
     |ORDER BY
     |   __sortBy__ __sortingDirection__,
     |   team_id __sortingDirection__
     |__limit__""".stripMargin

  override val rowParser: RowParser[TeamStreakTrophies] = TeamStreakTrophies.mapper

  override def execute(orderingKeyPath: OrderingKeyPath, parameters: RestStatisticsParameters)
                      (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamStreakTrophies]] = {
    import SqlBuilder.implicits._
    val sortBy = parameters.sortBy
    if(!sortingColumns.contains(sortBy))
      throw new Exception("Looks like SQL injection")

    val builder = new SqlBuilder("", newApi = true)
      .select(
        "league_id",
        "team_id",
        "team_name",
        "league_unit_id",
        "league_unit_name",
        "trophies_number",
        "number_of_victories",
        "number_of_undefeated"
      )
      .from("hattrick.team_details")
      .where
        .applyParameters(parameters)
        .applyParameters(orderingKeyPath)
        .round(parameters.statsType.asInstanceOf[Round].round)
      .orderBy(
        sortBy.to(parameters.sortingDirection),
        "team_id".to(parameters.sortingDirection)
      )

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
