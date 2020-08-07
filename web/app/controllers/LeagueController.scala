package controllers

import com.blackmorse.hattrick.model.enums.SearchType
import databases.ClickhouseDAO
import databases.clickhouse._
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import models.clickhouse._
import models.web
import models.web._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import service.{DefaultService, LeagueInfo, LeagueInfoService, OverviewStatsService}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class WebLeagueDetails(leagueInfo: LeagueInfo,
                            divisionLevelsLinks: Seq[(String, String)]) extends AbstractWebDetails

case class PromotionWithType(upDivisionLevel: Int, promoteType: String, promotions: List[Promotion])

@Singleton
class LeagueController @Inject() (val controllerComponents: ControllerComponents,
                                  implicit val clickhouseDAO: ClickhouseDAO,
                                  val leagueInfoService: LeagueInfoService,
                                  val defaultService: DefaultService,
                                  val viewDataFactory: ViewDataFactory,
                                  val overviewStatsService: OverviewStatsService,
                                  val hattrick: Hattrick) extends BaseController with I18nSupport with MessageSupport {

  private def stats[T](leagueId: Int,
                       statisticsParametersOpt: Option[StatisticsParameters],
                       sortColumn: String,
                       statisticsType: StatisticsType,
                       func: StatisticsParameters => Call,
                       statisticsCHRequest: StatisticsCHRequest[T],
                       viewFunc: ViewData[T, WebLeagueDetails] => Messages => play.twirl.api.HtmlFormat.Appendable,
                       selectedId: Option[Long] = None) = Action.async { implicit request =>
    val statsType = statisticsType match {
      case AvgMax => Avg
      case Accumulated => Accumulate
      case OnlyRound =>
        val currentRound = leagueInfoService.leagueInfo.currentRound(leagueId)
        Round(currentRound)
    }

    val (statisticsParameters, cookies) = defaultService.statisticsParameters(statisticsParametersOpt,
      leagueId = leagueId,
      statsType = statsType,
      sortColumn = sortColumn)

    val details = WebLeagueDetails(leagueInfo = leagueInfoService.leagueInfo(leagueId),
      divisionLevelsLinks = leagueInfoService.divisionLevelLinks(leagueId))

    statisticsCHRequest.execute(leagueId = Some(leagueId),
      statisticsParameters = statisticsParameters)
      .map(entities => viewDataFactory.create(details = details,
        func = func,
        statisticsType = statisticsType,
        statisticsParameters = statisticsParameters,
        statisticsCHRequest = statisticsCHRequest,
        entities = entities,
        selectedId = selectedId))
      .map(viewData => Ok(viewFunc(viewData).apply(messages)).withCookies(cookies: _*))
  }

  def bestTeams(leagueId: Int,
                statisticsParametersOpt: Option[StatisticsParameters],
                selectedTeamId: Option[Long]) = {
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "hatstats",
      statisticsType = AvgMax,
      func = sp => routes.LeagueController.bestTeams(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestHatstatsTeamRequest,
      viewFunc = { viewData: web.ViewData[TeamRating, WebLeagueDetails] => messages => views.html.league.bestTeams(viewData)(messages) },
      selectedId = selectedTeamId
    )
  }

  def bestLeagueUnits(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "hatstats",
      statisticsType = AvgMax,
      func = sp => routes.LeagueController.bestLeagueUnits(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestHatstatsLeagueRequest,
      viewFunc = {viewData: web.ViewData[LeagueUnitRating, WebLeagueDetails] => messages => views.html.league.bestLeagueUnits(viewData)(messages)}
    )

  def playerStats(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "scored",
      statisticsType = Accumulated,
      func = sp =>  routes.LeagueController.playerStats(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.playerStatsRequest,
      viewFunc = {viewData: web.ViewData[PlayerStats, WebLeagueDetails] => messages => views.html.league.playerStats(viewData)(messages)}
    )

  def teamState(leagueId: Int,
                statisticsParametersOpt: Option[StatisticsParameters],
                selectedTeam: Option[Long] = None) =
    stats(leagueId = leagueId,
      statisticsParametersOpt =  statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueController.teamState(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.teamStateRequest,
      viewFunc = {viewData: web.ViewData[TeamState, WebLeagueDetails] => messages => views.html.league.teamState(viewData)(messages)},
      selectedId = selectedTeam)

  def playerState(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueController.playerState(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.playerStateRequest,
      viewFunc = {viewData: web.ViewData[PlayersState, WebLeagueDetails] => messages =>views.html.league.playerState(viewData)(messages)}
    )

  def formalTeamStats(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "points",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueController.formalTeamStats(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.formalTeamStats,
      viewFunc = {viewData: web.ViewData[FormalTeamStats, WebLeagueDetails] => messages => views.html.league.formalTeamStats(viewData)(messages)}
    )

  def promotions(leagueId: Int) = Action.async { implicit request =>
    val details = WebLeagueDetails(leagueInfo = leagueInfoService.leagueInfo(leagueId),
      divisionLevelsLinks = leagueInfoService.divisionLevelLinks(leagueId))
    clickhouseDAO.promotions(season = leagueInfoService.leagueInfo.currentSeason(leagueId),
      leagueId = leagueId).map(promotions => {
      val promotionsWithType = promotions.groupBy(promotion => (promotion.upDivisionLevel, promotion.promoteType))
        .toSeq.sortBy(_._1)
        .map{case((upDivisionLevel, promoteType), promotions) => PromotionWithType(upDivisionLevel, promoteType, promotions)
        }

      Ok(views.html.league.promotions(details, promotionsWithType)(messages))
    })
  }

  def overview(leagueId: Int) = Action.async { implicit request =>
    val details = WebLeagueDetails(leagueInfo = leagueInfoService.leagueInfo(leagueId),
      divisionLevelsLinks = leagueInfoService.divisionLevelLinks(leagueId))

    val pageSize = request.cookies.get("hattid_page_size").map(_.value.toInt).getOrElse(DefaultService.PAGE_SIZE)

    overviewStatsService.overviewStatistics(Some(leagueId)).map(overviewStatistics =>
      Ok(views.html.league.leagueOverview(details, overviewStatistics, None,
        leagueInfoService.leagueInfo(leagueId).league, pageSize)))
  }

  def search(leagueId: Int) = Action.async { implicit request =>
    val details = WebLeagueDetails(leagueInfo = leagueInfoService.leagueInfo(leagueId),
      divisionLevelsLinks = leagueInfoService.divisionLevelLinks(leagueId))
    Future(Ok(views.html.league.searchPage(details, SearchForm.form, Seq())(messages)))
  }

  def processSearch(leagueId: Int) = Action.async{ implicit request =>
    SearchForm.form.bindFromRequest().fold(
      formWithErrors => {
        val details = WebLeagueDetails(leagueInfo = leagueInfoService.leagueInfo(leagueId),
          divisionLevelsLinks = leagueInfoService.divisionLevelLinks(leagueId))
        Future(BadRequest(views.html.league.searchPage(details, formWithErrors, Seq())(messages)))
      },
      form => Future.successful({
        Redirect(routes.LeagueController.searchResult(form.leagueId, form.teamName))
      })
    )
  }

  def searchResult(leagueId: Int, teamName: String) = Action.async { implicit request =>
    val details = WebLeagueDetails(leagueInfo = leagueInfoService.leagueInfo(leagueId),
      divisionLevelsLinks = leagueInfoService.divisionLevelLinks(leagueId))

    val teamsFuture = Future(hattrick.api.search()
      .searchType(SearchType.TEAMS).searchLeagueId(leagueId).searchString(teamName)
        .execute())

    teamsFuture.map(teams => {
      val seq = Option(teams.getSearchResults.asScala)
        .map(results => results.map(result => (result.getResultId.toLong, result.getResultName)))
          .getOrElse(Seq())

      Ok(views.html.league.searchPage(details, SearchForm.form, seq)(messages))
    })
  }
}

case class SearchForm(teamName: String, leagueId: Int)

object SearchForm {
  val form: Form[SearchForm] = Form(
    mapping(
      "teamName" -> text.verifying(nonEmpty),
       "leagueId" -> number
    )(SearchForm.apply)(SearchForm.unapply)
  )
}