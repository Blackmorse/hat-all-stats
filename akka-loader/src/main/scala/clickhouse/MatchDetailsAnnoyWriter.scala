package clickhouse

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import com.typesafe.config.Config
import chpp.worlddetails.models.League
import chpp.commonmodels.MatchType
import com.crobox.clickhouse.ClickhouseClient
import scala.concurrent.Future

@Singleton
class MatchDetailsAnnoyWriter @Inject()(val config: Config, val actorSystem: ActorSystem) {
  import actorSystem.dispatcher

  private val client = new ClickhouseClient(Some(config))
  private val databaseName = config.getString("database_name")

  def insertIntoAnnoyMatchDetails(league: League, matchType: MatchType.Value): Future[String] = {

    val round = if (matchType == MatchType.LEAGUE_MATCH) league.matchRound - 1 else if (matchType == MatchType.CUP_MATCH) league.matchRound
    val cupLevelIndexCondition = if (matchType == MatchType.LEAGUE_MATCH) "cup_level_index = 0" else "cup_level_index > 0"
    val season = league.season - league.seasonOffset

    val sql = s"""
      |INSERT INTO ${databaseName}.match_details_annoy SELECT
      |    match_id,
      |    goals,
      |    enemy_goals,
      |    tactic_type,
      |    tactic_skill,
      |    opposite_tactic_type,
      |    opposite_tactic_skill,
      |    rating_indirect_set_pieces_def,
      |    rating_indirect_set_pieces_att,
      |    opposite_rating_indirect_set_pieces_def,
      |    rating_indirect_set_pieces_att,
      |    [rates(rating_midfield, opposite_rating_midfield), rates(rating_left_def, opposite_rating_right_att), rates(rating_mid_def, opposite_rating_mid_att), rates(rating_right_def, opposite_rating_left_att)] AS vector
      |FROM ${databaseName}.match_details
      |WHERE league_id = ${league.leagueId} AND season = $season AND round = ${round} AND $cupLevelIndexCondition
    """.stripMargin

    client.execute(sql)
  }
}
