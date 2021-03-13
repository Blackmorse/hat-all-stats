package databases.requests

import databases.SqlBuilder
import databases.dao.RestClickhouseDAO
import models.web.Desc

import scala.concurrent.Future

trait ClickhouseOverviewRequest[T] extends ClickhouseRequest[T] {
  val sql: String
  val limit = 5
  def sortBy: String = ""

  def execute(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int])
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[T]] = {

    val builder = SqlBuilder(sql)
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
      .and
        .page(0)
        .pageSize(limit)
      .sortBy(sortBy)
      .sortingDirection(Desc)

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
