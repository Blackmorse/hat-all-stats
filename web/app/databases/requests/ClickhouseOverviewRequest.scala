package databases.requests

import databases.dao.RestClickhouseDAO
import sqlbuilder.SqlBuilder
import databases.dao.SqlBuilderParameters

import scala.concurrent.Future

trait ClickhouseOverviewRequest[T] extends ClickhouseRequest[T] {
  val limit = 5

  protected def builder(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): SqlBuilder

  def execute(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int])
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[T]] = {
    restClickhouseDAO.execute(builder(season, round, leagueId, divisionLevel).sqlWithParameters().build, rowParser)
  }
}
