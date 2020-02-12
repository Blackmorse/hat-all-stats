package databases

import anorm.{NamedParameter, ParameterValue, Row, SQL, SimpleSql}
import service.DefaultService

import scala.collection.mutable


case class SqlBuilder(baseSql: String) {
  private val params: mutable.Buffer[(String, ParameterValue)] = mutable.Buffer()
  private var page = 0
  private val pageSize = DefaultService.PAGE_SIZE

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

  def page(page: Int): SqlBuilder = {
    this.page = page
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

    SQL(sql)
      .on(params.map(NamedParameter.namedWithString): _*)
  }
}