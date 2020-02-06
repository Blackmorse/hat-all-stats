package databases

import akka.actor.ActorSystem
import com.google.inject.Inject
import databases.clickhouse.StatisticsCHRequest
import javax.inject.Singleton
import models.clickhouse.{LeagueSeasons, TeamMatchInfo}
import models.web.{Accumulate, MultiplyRoundsType, Round, StatsType}
import play.api.db.DBApi
import play.api.libs.concurrent.CustomExecutionContext

import scala.concurrent.Future

@Singleton
class ClickhouseDAO @Inject()(dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db = dbApi.database("default")

  def execute[T](request: StatisticsCHRequest[T],
                 leagueId: Option[Int] = None,
                 season: Option[Int] = None,
                 divisionLevel: Option[Int] = None,
                 leagueUnitId: Option[Long] = None,
                 teamId: Option[Long] = None,
                 page: Int = 0,
                 statsType: StatsType,
                 sortBy: String) = Future {
    db.withConnection { implicit connection =>

      if (!request.sortingColumns.contains(sortBy)) throw new Exception("Looks like SQL Injection")

      val sql = statsType match {
        case MultiplyRoundsType(func) => request.aggregateSql.replace("__func__", func).replace("__sortBy__", sortBy)
        case Accumulate => request.aggregateSql.replace("__sortBy__", sortBy)
        case Round(round) => request.oneRoundSql.replace("__round__", round.toString).replace("__sortBy__", sortBy)
      }

      val builder = SqlBuilder(sql)

      leagueId.foreach(builder.leagueId)
      season.foreach(builder.season)
      divisionLevel.foreach(builder.divisionLevel)
      leagueUnitId.foreach(builder.leagueUnitId)
      teamId.foreach(builder.teamId)
      builder.page(page)

      builder.build.as(request.parser.*)
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

@Singleton
class DatabaseExecutionContext @Inject()(system: ActorSystem) extends CustomExecutionContext(system, "database.clickhouse")

