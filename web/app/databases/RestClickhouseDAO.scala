package databases

import databases.requests.teamrankings.TeamRankingsRequest
import databases.requests.{AllRequest, AvgMaxRequest, ClickhouseRequest, ClickhouseStatisticsRequest, OrderingKeyPath, RoundRequest}
import javax.inject.{Inject, Singleton}
import models.web.{Accumulate, MultiplyRoundsType, RestStatisticsParameters, RestTableData, Round}
import play.api.db.DBApi
import play.api.libs.json.{Json, Writes}

import scala.concurrent.Future

@Singleton
class RestClickhouseDAO @Inject()(dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db = dbApi.database("default")

  def executeStatisticsRequest[T](clickhouseRequest: ClickhouseStatisticsRequest[T],
                                  parameters: RestStatisticsParameters,
                                  orderingKeyPath: OrderingKeyPath)(implicit tjs: Writes[T]) = Future {
    db.withConnection { implicit connection =>
      val sql = parameters.statsType match {
        case st@MultiplyRoundsType(_) =>
          clickhouseRequest.asInstanceOf[AvgMaxRequest[T]]
            .avgMaxRequest(orderingKeyPath, st, parameters)
        case Round(_) =>
          clickhouseRequest.asInstanceOf[RoundRequest[T]]
            .roundRequest(orderingKeyPath,  parameters)
        case Accumulate =>
          clickhouseRequest.asInstanceOf[AllRequest[T]]
            .allRequest(orderingKeyPath, parameters)
      }

      sql.as(clickhouseRequest.rowParser.*)
    }
  }.map(result => {
      val isLastPage = result.size <= parameters.pageSize

      val entities = if(!isLastPage) result.dropRight(1) else result
      val restTableData = RestTableData(entities, isLastPage)
      Json.toJson(restTableData)
    })

  def executeTeamRankingsRequest(orderingKeyPath: OrderingKeyPath) = Future {
    db.withConnection { implicit connection =>
      TeamRankingsRequest.execute(orderingKeyPath).as(TeamRankingsRequest.rowParser.*)
    }
  }
}
