package databases

import akka.actor.ActorSystem
import javax.inject.Singleton
import anorm._
import anorm.SqlParser.{get, scalar}
import com.google.inject.Inject
import play.api.db.DBApi
import play.api.libs.concurrent.CustomExecutionContext

import scala.concurrent.Future

case class AvgTeamRating(teamId: Long, teamName: String, hatStats: Int)

@Singleton
class ClickhouseDAO @Inject()(dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db = dbApi.database("default")

  private val simple = {
    get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Int]("hatstats") map {
      case teamId ~ teamName ~ hatstats => AvgTeamRating(teamId, teamName, hatstats)
    }
  }

  def bestTeams = Future {
    db.withConnection{implicit connection =>
      SQL("select team_id, team_name, toInt32(avg(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats from hattrick.match_details where division_level = 1 group by team_id, team_name order by hatstats desc limit 8")
        .as(simple.*)
    }
  }
}

@Singleton
class DatabaseExecutionContext @Inject()(system: ActorSystem) extends CustomExecutionContext(system, "database.clickhouse")

