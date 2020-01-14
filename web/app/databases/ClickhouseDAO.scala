package databases

import akka.actor.ActorSystem
import javax.inject.Singleton
import anorm._
import anorm.SqlParser.{get, scalar}
import com.google.inject.Inject
import play.api.db.DBApi
import play.api.libs.concurrent.CustomExecutionContext
import models.clickhouse.AvgTeamRating

import scala.concurrent.Future

@Singleton
class ClickhouseDAO @Inject()(dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db = dbApi.database("default")

  private val avgTeamRatingMapper = {
    get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("hatstats") map {
      case teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ hatstats =>
        AvgTeamRating(teamId, teamName, leagueUnitId, leagueUnitName, hatstats)
    }
  }

  def bestTeamsForLeague(leagueId: Int) = Future {
    db.withConnection{implicit connection =>
      SQL(s"select team_id, team_name, league_unit_id, league_unit_name, toInt32(avg(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats from hattrick.match_details where league_id = $leagueId group by team_id, team_name, league_unit_id, league_unit_name order by hatstats desc limit 8")
        .as(avgTeamRatingMapper.*)
    }
  }


}

@Singleton
class DatabaseExecutionContext @Inject()(system: ActorSystem) extends CustomExecutionContext(system, "database.clickhouse")

