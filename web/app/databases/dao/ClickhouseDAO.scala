package databases.dao

import akka.actor.ActorSystem
import databases.sqlbuilder.SqlBuilder
import models.clickhouse._
import play.api.db.DBApi
import play.api.libs.concurrent.CustomExecutionContext

import javax.inject.{Inject, Singleton}

@Singleton
class ClickhouseDAO @Inject()(dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db = dbApi.database("default")


  def historyInfo(leagueId: Option[Int], season: Option[Int], round: Option[Int]): List[HistoryInfo] = {
    db.withConnection { implicit connection =>
      SqlBuilder( """SELECT
                      |    season,
                      |    league_id,
                      |    division_level,
                      |    round,
                      |    count() AS cnt
                      |FROM hattrick.match_details
                      | __where__
                      |GROUP BY
                      |    season,
                      |    league_id,
                      |    division_level,
                      |    round
                      |ORDER BY
                      |    season ASC,
                      |    league_id ASC,
                      |    division_level ASC,
                      |    round ASC
                      |""".stripMargin)
          .where
            .leagueId(leagueId)
            .season(season)
            .round(round)
        .build.as(HistoryInfo.mapper.*)
    }
  }

  def historyTeamLeagueUnitInfo(season: Int, leagueId: Int, teamId: Long): Option[HistoryTeamLeagueUnitInfo] = {
    db.withConnection{ implicit  connection =>
      val builder = SqlBuilder("""
           |SELECT
           |    division_level,
           |    league_unit_id
           |FROM hattrick.team_rankings
           |__where__
           |LIMIT 1
           |""".stripMargin)
        .where.season(season).leagueId(leagueId).teamId(teamId)

      builder.build.as(HistoryTeamLeagueUnitInfo.historyTeamLeagueUnitInfoMapper.singleOpt)
    }
  }

}

@Singleton
class DatabaseExecutionContext @Inject()(system: ActorSystem) extends CustomExecutionContext(system, "database.clickhouse")

