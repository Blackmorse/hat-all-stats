package databases

import anorm.{NamedParameter, ParameterValue, Row, SQL, SimpleSql}
import databases.requests.OrderingKeyPath
import databases.requests.model.Role
import models.web.{Asc, Desc, RestStatisticsParameters, SortingDirection}
import service.DefaultService

import scala.collection.mutable

abstract class Clause(sqlBuilder: SqlBuilder) {
  private[databases] val params: mutable.Buffer[(String, ParameterValue)] = mutable.Buffer()

  def applyParameters(orderingKeyPath: OrderingKeyPath): this.type = {
    orderingKeyPath.leagueId.foreach(this.leagueId)
    orderingKeyPath.divisionLevel.foreach(this.divisionLevel)
    orderingKeyPath.leagueUnitId.foreach(this.leagueUnitId)
    orderingKeyPath.teamId.foreach(this.teamId)

    this
  }

  def season(season: Int): this.type = {
    params += (("season", season))
    this
  }

  def season(seasonOpt: Option[Int]): this.type = {
    seasonOpt.foreach(season => params += (("season", season)))
    this
  }

  def leagueId(leagueIdOpt: Option[Int]): this.type = {
    leagueIdOpt.foreach(leagueId => params += (("league_id", leagueId)))
    this
  }

  def leagueId(leagueId: Int): this.type = {
    params += (("league_id", leagueId))
    this
  }

  def divisionLevel(divisionLevelOpt: Option[Int]): this.type = {
    divisionLevelOpt.foreach(divisionLevel => params += (("division_level", divisionLevel)))
    this
  }

  def divisionLevel(divisionLevel: Int): this.type = {
    params += (("division_level", divisionLevel))
    this
  }

  def leagueUnitId(leagueUnitId: Long): this.type = {
    params += (("league_unit_id", leagueUnitId))
    this
  }

  def teamId(teamId: Long): this.type = {
    params += (("team_id", teamId))
    this
  }

  def teamId(teamIdOpt: Option[Long]): this.type = {
    teamIdOpt.foreach(teamId => params += (("team_id", teamId)))
    this
  }

  def round(round: Int): this.type = {
    params += (("round", round))
    this
  }

  def role(roleOpt: Option[Role]): this.type = {
    roleOpt.foreach(role => params += (("role", role.name)))
    this
  }

  def nationality(nationalityOpt: Option[Int]): this.type = {
    nationalityOpt.foreach(nationality => params += (("nationality", nationality)))
    this
  }

  def nationality(nationality: Int): this.type = {
    params += (("nationality", nationality))
    this
  }

  def and: SqlBuilder = sqlBuilder

  def build: SimpleSql[Row] = sqlBuilder.build

  def where = sqlBuilder.where

  def having = sqlBuilder.having
}

class WhereClause(sqlBuilder: SqlBuilder) extends Clause(sqlBuilder)

class HavingClause(sqlBuilder: SqlBuilder) extends Clause(sqlBuilder)

case class SqlBuilder(baseSql: String) {
  private var page = 0
  private var pageSize = DefaultService.PAGE_SIZE
  private var sortingDirection: String = "desc"
  private val whereClause = new WhereClause(this)
  private val havingClause = new HavingClause(this)

  def where = whereClause
  def having = havingClause

  def applyParameters(orderingKeyPath: OrderingKeyPath): SqlBuilder = {
    orderingKeyPath.leagueId.foreach(this.whereClause.leagueId)
    orderingKeyPath.divisionLevel.foreach(this.whereClause.divisionLevel)
    orderingKeyPath.leagueUnitId.foreach(this.whereClause.leagueUnitId)
    orderingKeyPath.teamId.foreach(this.whereClause.teamId)

    this
  }

  def applyParameters(parameters: RestStatisticsParameters): SqlBuilder = {
    whereClause.season(parameters.season)
    page(parameters.page)
    pageSize(parameters.pageSize)
    sortingDirection(parameters.sortingDirection)

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
    val sql =  if(where.params.nonEmpty) {
      val wheresql = " WHERE " + where.params.map{case (name, _) => s"$name = {$name}"}.mkString(" and ")
      baseSql.replace("__where__", wheresql)
    } else {
      baseSql.replace("__where__", " ")
    }

    val hSql = if(having.params.nonEmpty) {
      val havingSql = " HAVING " + having.params.map{case (name, _) => s"$name = {$name}"}.mkString(" and ")
      sql.replace("__having__", havingSql)
    } else {
      sql.replace("__having__", " ")
    }

    val result = hSql
      .replace("__limit__", s" limit ${page * pageSize}, ${pageSize + 1}")
      .replace("__sortingDirection__", sortingDirection)

    SQL(result)
      .on((where.params ++ having.params).map(NamedParameter.namedWithString): _*)
  }
}