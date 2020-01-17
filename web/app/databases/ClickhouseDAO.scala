package databases

import akka.actor.ActorSystem
import javax.inject.Singleton
import anorm._
import com.google.inject.Inject
import models.clickhouse.league.{LeagueUnitRating, TeamRating}
import play.api.db.DBApi
import play.api.libs.concurrent.CustomExecutionContext

import scala.collection.mutable
import scala.concurrent.Future

@Singleton
class ClickhouseDAO @Inject()(dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db = dbApi.database("default")

  def bestTeams(leagueId: Option[Int] = None, season: Option[Int] = None, divisionLevel: Option[Int] = None) = Future {
    db.withConnection{implicit connection =>
      val matchDetailsSql = SqlBuilder("""select team_id,
                        |team_name,
                        |league_unit_id,
                        |league_unit_name,
                        |toInt32(avg(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats,
                        |toInt32(avg(rating_midfield)) as midfield,
                        |toInt32(avg((rating_right_def + rating_left_def + rating_mid_def)/3)) as defense,
                        |toInt32(avg( (rating_right_att + rating_mid_att + rating_left_att)/3)) as attack
                        |from hattrick.match_details __where__
                        |group by team_id, team_name, league_unit_id, league_unit_name order by hatstats desc limit 8""".stripMargin)

      leagueId.foreach(matchDetailsSql.leagueId)
      season.foreach(matchDetailsSql.season)
      divisionLevel.foreach(matchDetailsSql.divisionLevel)

      matchDetailsSql.build.as(TeamRating.teamRatingMapper.*)
    }
  }

  def bestLeagueUnits(leagueId: Option[Int] = None, season: Option[Int] = None, divisionLevel: Option[Int] = None) = Future {
    db.withConnection{ implicit connection =>
      val matchDetailsSql = SqlBuilder("""select league_unit_id,
                        |league_unit_name,
                        |toInt32(avg(hatstats)) as hatstats from
                        |   (select league_unit_id,
                        |     league_unit_name,
                        |     round,
                        |     toInt32(avg(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats
                        |     from hattrick.match_details
                        |     __where__
                        |     group by league_unit_id, league_unit_name, round)
                        |group by league_unit_id, league_unit_name order by hatstats desc limit 8""".stripMargin)

      leagueId.foreach(matchDetailsSql.leagueId)
      season.foreach(matchDetailsSql.season)
      divisionLevel.foreach(matchDetailsSql.divisionLevel)

      matchDetailsSql.build.as(LeagueUnitRating.leagueUnitRatingMapper.*)
    }
  }

}

case class SqlBuilder(baseSql: String) {
  private val params: mutable.Buffer[(String, ParameterValue)] = mutable.Buffer()

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

  def build: SimpleSql[Row] = {
    val sql =  if(params.nonEmpty) {
      val where = " where " + params.map{case (name, _) => s"$name = {$name}"}.mkString(" and ")
      baseSql.replace("__where__", where)
    } else {
      baseSql.replace("__where__", " ")
    }

    SQL(sql)
      .on(params.map(NamedParameter.namedWithString): _*)
  }

}

@Singleton
class DatabaseExecutionContext @Inject()(system: ActorSystem) extends CustomExecutionContext(system, "database.clickhouse")

