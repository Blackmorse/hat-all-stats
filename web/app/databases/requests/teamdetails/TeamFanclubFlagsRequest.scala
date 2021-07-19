package databases.requests.teamdetails

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import databases.requests.model.team.TeamFanclubFlags
import databases.sqlbuilder.SqlBuilder
import models.web.{RestStatisticsParameters, Round}

import scala.concurrent.Future

object TeamFanclubFlagsRequest extends ClickhouseStatisticsRequest[TeamFanclubFlags] {
  override val sortingColumns: Seq[String] = Seq("fanclub_size", "home_flags", "away_flags", "all_flags")
  override val aggregateSql: String = ""

  override val oneRoundSql: String = """
       |SELECT
       |    league_id,
       |    team_id,
       |    team_name,
       |    league_unit_id,
       |    league_unit_name,
       |    fanclub_size,
       |    home_flags,
       |    away_flags,
       |    home_flags + away_flags AS all_flags
       |FROM hattrick.team_details
       | __where__
       |ORDER BY
       |   __sortBy__ __sortingDirection__,
       |   team_id __sortingDirection__
       |__limit__
       |""".stripMargin

  override val rowParser: RowParser[TeamFanclubFlags] = TeamFanclubFlags.mapper

  override def execute(orderingKeyPath: OrderingKeyPath, parameters: RestStatisticsParameters)
                      (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamFanclubFlags]] = {
    val sortBy = parameters.sortBy
    if(!sortingColumns.contains(sortBy))
      throw new Exception("Looks like SQL injection")

    import SqlBuilder.implicits._
    val builder = new SqlBuilder("", newApi = true)
      .select(
        "league_id",
        "team_id",
        "team_name",
        "league_unit_id",
        "league_unit_name",
        "fanclub_size",
        "home_flags",
        "away_flags",
        "home_flags + away_flags" as "all_flags"
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
