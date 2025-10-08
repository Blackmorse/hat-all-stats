package databases.requests

import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.DBIO
import sqlbuilder.SqlBuilder
import zio.ZIO

trait ClickhouseOverviewRequest[T] extends ClickhouseRequest[T] {
  val limit = 5

  protected def builder(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): SqlBuilder
  
  def executeZio(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): DBIO[List[T]] = wrapErrors {
    ZIO.serviceWithZIO[RestClickhouseDAO] { dao =>
      dao.executeZIO(builder(season, round, leagueId, divisionLevel).sqlWithParameters().build, rowParser)
    }
  }
}
