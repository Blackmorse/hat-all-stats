package controllers


import javax.inject.Inject
import akka.stream.Materializer
import databases.dao.InsertClickhouseDAO
import play.api.Logging
import play.api.mvc._
import service.RequestCounterService

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class RequestSourceFilter @Inject() (implicit val mat: Materializer,
                                     ec: ExecutionContext,
                                     requestCounterService: RequestCounterService) extends Filter with Logging {
  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    requestHeader.headers.get("hattid-request-source").filter(_ == "HO!").foreach(_ => requestCounterService.hoRequest)
    nextFilter(requestHeader)
  }
}

class LogUrlsFilter @Inject()(implicit val mat: Materializer,
                              insertClickhouseDAO: InsertClickhouseDAO
                             ) extends Filter with Logging {
  override def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    if (requestHeader.path.startsWith("/api")) {
      val params = requestHeader.queryString.map{case(key, values) => (key, values.head)}.toSeq
      insertClickhouseDAO.requestLog(requestHeader.path, params)
    }
    nextFilter(requestHeader)
  }
}

import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import play.api.http.EnabledFilters

class Filters @Inject() (
                          defaultFilters: EnabledFilters,
                          requestSourceFilter: RequestSourceFilter,
                          logUrlsFilter: LogUrlsFilter
                        ) extends DefaultHttpFilters(defaultFilters.filters :+ requestSourceFilter :+ logUrlsFilter: _*)