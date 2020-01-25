package databases

import akka.actor.ActorSystem
import javax.inject.Singleton
import anorm._
import com.google.inject.Inject
import models.clickhouse.{LeagueSeasons, LeagueUnitRating, TeamMatchInfo, TeamRating}
import play.api.db.DBApi
import play.api.libs.concurrent.CustomExecutionContext
import service.DefaultService

import scala.collection.mutable
import scala.concurrent.Future

@Singleton
class ClickhouseDAO @Inject()(dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db = dbApi.database("default")

  def bestTeams(leagueId: Option[Int] = None,
                season: Option[Int] = None,
                divisionLevel: Option[Int] = None,
                leagueUnitId: Option[Long] = None,
                page: Int = 0) = Future {
    db.withConnection{implicit connection =>
      val matchDetailsSql = SqlBuilder("""select team_id,
                        |team_name,
                        |league_unit_id,
                        |league_unit_name,
                        |toInt32(avg(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats,
                        |toInt32(avg(rating_midfield)) as midfield,
                        |toInt32(avg((rating_right_def + rating_left_def + rating_mid_def) / 3)) as defense,
                        |toInt32(avg( (rating_right_att + rating_mid_att + rating_left_att) / 3)) as attack
                        |from hattrick.match_details __where__ and rating_midfield + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att != 0
                        |group by team_id, team_name, league_unit_id, league_unit_name order by hatstats desc __limit__""".stripMargin)

      leagueId.foreach(matchDetailsSql.leagueId)
      season.foreach(matchDetailsSql.season)
      divisionLevel.foreach(matchDetailsSql.divisionLevel)
      leagueUnitId.foreach(matchDetailsSql.leagueUnitId)
      matchDetailsSql.page(page)



      matchDetailsSql.build.as(TeamRating.teamRatingMapper.*)
    }
  }

  def bestLeagueUnits(leagueId: Option[Int] = None,
                      season: Option[Int] = None,
                      divisionLevel: Option[Int] = None,
                      leagueUnitId: Option[Long] = None,
                      page: Int = 0) = Future {
    db.withConnection{ implicit connection =>
      val leagueUnitsSql = SqlBuilder("""select league_unit_id,
                        |league_unit_name,
                        |toInt32(avg(hatstats)) as hatstats,
                        |toInt32(avg(midfield)) as midfield,
                        |toInt32(avg(defense)) as defense,
                        |toInt32(avg(attack)) as attack
                        | from
                        |   (select league_unit_id,
                        |     league_unit_name,
                        |     round,
                        |     toInt32(avg(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats,
                        |     toInt32(avg(rating_midfield)) as midfield,
                        |     toInt32(avg((rating_right_def + rating_left_def + rating_mid_def) / 3)) as defense,
                        |     toInt32(avg((rating_right_att + rating_mid_att + rating_left_att) / 3)) as attack
                        |     from hattrick.match_details
                        |     __where__ and rating_midfield + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att != 0
                        |     group by league_unit_id, league_unit_name, round)
                        |group by league_unit_id, league_unit_name order by hatstats desc __limit__""".stripMargin)

      leagueId.foreach(leagueUnitsSql.leagueId)
      season.foreach(leagueUnitsSql.season)
      divisionLevel.foreach(leagueUnitsSql.divisionLevel)
      leagueUnitId.foreach(leagueUnitsSql.leagueUnitId)
      leagueUnitsSql.page(page)

      leagueUnitsSql.build.as(LeagueUnitRating.leagueUnitRatingMapper.*)
    }
  }

  def teamMatchesForSeason(season: Int, leagueId: Int, divisionLevel: Int, leagueUnitId: Long, teamId: Long) = Future {
    db.withConnection { implicit connection =>
      val teamSql = SqlBuilder(
        """SELECT
          |    round,
          |    match_id,
          |    team_id,
          |    team_name,
          |    toInt32(((((((rating_midfield * 3) + rating_right_def) + rating_left_def) + rating_mid_def) + rating_right_att) + rating_mid_att) + rating_left_att) AS hatstats,
          |    formation,
          |    dt
          |FROM hattrick.match_details
          | __where__
          |ORDER BY round ASC
        """.stripMargin)
        .season(season).leagueId(leagueId).divisionLevel(divisionLevel).leagueUnitId(leagueUnitId).teamId(teamId)
        .build

      teamSql.as(TeamMatchInfo.teamMatchInfoMapper.*)
    }
  }

  def seasonsForLeagues() =  {
    db.withConnection { implicit connection =>
      SqlBuilder("SELECT DISTINCT league_id, season from hattrick.match_details").build
        .as(LeagueSeasons.mapper.*)
    }
  }

}

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
    .replace("__limit__", s" limit ${page * pageSize}, $pageSize")

    SQL(sql)
      .on(params.map(NamedParameter.namedWithString): _*)
  }

}

@Singleton
class DatabaseExecutionContext @Inject()(system: ActorSystem) extends CustomExecutionContext(system, "database.clickhouse")

