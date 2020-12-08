package databases

import anorm.{NamedParameter, ParameterValue, Row, SQL, SimpleSql}
import databases.requests.OrderingKeyPath
import models.web.{Asc, Desc, RestStatisticsParameters, Round, SortingDirection}
import service.DefaultService

import scala.collection.mutable


case class SqlBuilder(baseSql: String) {
  private val params: mutable.Buffer[(String, ParameterValue)] = mutable.Buffer()
  private var page = 0
  private var pageSize = DefaultService.PAGE_SIZE
  private var sortingDirection: String = "desc"

  def applyParameters(orderingKeyPath: OrderingKeyPath): SqlBuilder = {
    orderingKeyPath.leagueId.foreach(this.leagueId)
    orderingKeyPath.divisionLevel.foreach(this.divisionLevel)
    orderingKeyPath.leagueUnitId.foreach(this.leagueUnitId)
    orderingKeyPath.teamId.foreach(this.teamId)

    this
  }

  def applyParameters(parameters: RestStatisticsParameters): SqlBuilder = {
    season(parameters.season)
    page(parameters.page)
    pageSize(parameters.pageSize)
    sortingDirection(parameters.sortingDirection)

    this
  }

  def season(season: Int): SqlBuilder = {
    params += (("season", season))
    this
  }
  def leagueId(leagueId: Int): SqlBuilder = {
    params += (("league_id", leagueId))
    this
  }
  def divisionLevel(divisionLevel: Int): SqlBuilder = {
    params += (("division_level", divisionLevel))
    this
  }

  def leagueUnitId(leagueUnitId: Long): SqlBuilder = {
    params += (("league_unit_id", leagueUnitId))
    this
  }

  def teamId(teamId: Long): SqlBuilder = {
    params += (("team_id", teamId))
    this
  }

  def round(round: Int): SqlBuilder = {
    params += (("round", round))
    this
  }

  def page(page: Int): SqlBuilder = {
    this.page = page
    this
  }

  def pageSize(pageSize: Int): SqlBuilder = {
    this.pageSize = pageSize
    this
  }

  def sortingDirection(direction: SortingDirection): SqlBuilder = {
    this.sortingDirection = direction match {
      case Desc => "desc"
      case Asc => "asc"
    }
    this
  }

  def build: SimpleSql[Row] = {
    val sql =  (if(params.nonEmpty) {
      val where = " where " + params.map{case (name, _) => s"$name = {$name}"}.mkString(" and ")
      baseSql.replace("__where__", where)
    } else {
      baseSql.replace("__where__", " ")
    })
      .replace("__limit__", s" limit ${page * pageSize}, ${pageSize + 1}")
      .replace("__sortingDirection__", sortingDirection)

    SQL(sql)
      .on(params.map(NamedParameter.namedWithString): _*)
  }
}