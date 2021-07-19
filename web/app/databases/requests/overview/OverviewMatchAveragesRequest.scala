package databases.requests.overview

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.OverviewMatchAverages
import databases.sqlbuilder.SqlBuilder

import scala.concurrent.Future

object OverviewMatchAveragesRequest extends ClickhouseOverviewRequest[OverviewMatchAverages] {
  override val sql: String = """
     |SELECT
     |    toUInt32(avgIf(sold_total, is_home_match = 'home')) AS avg_sold_total,
     |    toUInt16(avg(rating_midfield * 3 + rating_left_att + rating_right_att + rating_mid_att + rating_left_def + rating_right_def + rating_mid_def)) AS avg_hatstats,
     |    avg(goals) AS avg_goals
     |FROM hattrick.match_details
     |__where__
     |""".stripMargin

  override val rowParser: RowParser[OverviewMatchAverages] = OverviewMatchAverages.mapper

  override def execute(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int])
                      (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[OverviewMatchAverages]] = {
    import SqlBuilder.implicits._
    import SqlBuilder.fields._

    val builder = new SqlBuilder("", newApi = true)
      .select(
        "toUInt32(avgIf(sold_total, is_home_match = 'home'))" as "avg_sold_total",
        s"toUInt16(avg($hatstats))" as "avg_hatstats",
        "avg(goals)" as "avg_goals"
      )
      .from("hattrick.match_details")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
