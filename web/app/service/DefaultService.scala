package service

import play.api.mvc.Request
import play.api.mvc.AnyContent
import models.web.StatisticsParameters
import com.google.inject.Inject

import play.api.mvc.Cookie
import models.web.StatsType
import models.web.Desc

class DefaultService @Inject() (val leagueInfoService: LeagueInfoService) {

  def statisticsParameters(statisticsParametersOpt: Option[StatisticsParameters],
      leagueId: Int,
      statsType: StatsType,
      sortColumn: String)
      (implicit request: Request[AnyContent]): (StatisticsParameters, Seq[Cookie]) = {
    
      if (statisticsParametersOpt.isDefined) {
        (statisticsParametersOpt.get, 
        Seq(Cookie("hattid_page_size", statisticsParametersOpt.get.pageSize.toString(), sameSite = Some(Cookie.SameSite.Lax), httpOnly = false)))
      } else {
        val pageSize = request.cookies.get("hattid_page_size").map(_.value.toInt).getOrElse(DefaultService.PAGE_SIZE)

        (StatisticsParameters(
          season = leagueInfoService.leagueInfo.currentSeason(leagueId),
          page = 0,
          statsType = statsType,
          sortBy = sortColumn,
          pageSize = pageSize,
          sortingDirection = Desc
        ), Seq())
      }
  }
}

object DefaultService {
  val PAGE_SIZE = 16

}
