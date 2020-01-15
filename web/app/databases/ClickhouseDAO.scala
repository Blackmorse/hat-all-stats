package databases

import akka.actor.ActorSystem
import javax.inject.Singleton
import anorm._
import com.google.inject.Inject
import models.clickhouse.league.{LeagueUnitRating, TeamRating}
import play.api.db.DBApi
import play.api.libs.concurrent.CustomExecutionContext

import scala.concurrent.Future

@Singleton
class ClickhouseDAO @Inject()(dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db = dbApi.database("default")

  def bestTeamsForLeague(leagueId: Int, season: Int) = Future {
    db.withConnection{implicit connection =>
      SQL(
        s"""select team_id,
           |team_name,
           |league_unit_id,
           |league_unit_name,
           |toInt32(avg(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats,
           |toInt32(avg(rating_midfield)) as midfield,
           |toInt32(avg((rating_right_def + rating_left_def + rating_mid_def)/3)) as defense,
           |toInt32(avg( (rating_right_att + rating_mid_att + rating_left_att)/3)) as attack
           |from hattrick.match_details where league_id = $leagueId and season=$season
           |group by team_id, team_name, league_unit_id, league_unit_name order by hatstats desc limit 8""".stripMargin)
        .as(TeamRating.teamRatingMapper.*)
    }
  }

  def bestLeagueUnitsForLeague(leagueId: Int, season: Int) = Future {
    db.withConnection{ implicit connection =>
      SQL(s"select league_unit_id, league_unit_name, toInt32(avg(hatstats)) as hatstats from (select league_unit_id, league_unit_name, round, toInt32(avg(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats from hattrick.match_details where league_id=$leagueId and season=$season  group by league_unit_id, league_unit_name, round) group by league_unit_id, league_unit_name order by hatstats desc limit 8")
        .as(LeagueUnitRating.leagueUnitRatingMapper.*)
    }
  }

}

@Singleton
class DatabaseExecutionContext @Inject()(system: ActorSystem) extends CustomExecutionContext(system, "database.clickhouse")

