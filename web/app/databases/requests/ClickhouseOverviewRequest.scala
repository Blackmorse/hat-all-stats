package databases.requests

import databases.{RestClickhouseDAO, SqlBuilder}

import scala.concurrent.Future

trait ClickhouseOverviewRequest[T] extends ClickhouseRequest[T] {
  val sql: String
  private val limit = 5

  def execute(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int])
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[T]] = {

    val builder = SqlBuilder(sql)
      .round(round)
      .season(season)
      .page(0)
      .pageSize(limit)
    leagueId.foreach(builder.leagueId)
    divisionLevel.foreach(builder.divisionLevel)

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
