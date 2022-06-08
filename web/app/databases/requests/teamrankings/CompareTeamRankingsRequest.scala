package databases.requests.teamrankings

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest
import databases.requests.ClickhouseRequest.implicits.SqlWithParametersExtended
import models.clickhouse.TeamRankings

import scala.concurrent.Future

object CompareTeamRankingsRequest extends ClickhouseRequest[TeamRankings] {
  override val rowParser: RowParser[TeamRankings] = TeamRankings.teamRankingsMapper

  def execute(teamId1: Long, teamId2: Long, fromSeason: Int, fromRound: Int)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamRankings]] = {
    val builder = TeamRankingsRequest.select
      //(season = fromSeason && round >= fromRound) OR (season >= fromSeason + 1) AND
      //(team_id = teamId1 OR team_id = teamId2) AND rank_type = league_id
      //-- with opened brackets
      .where
         .season(fromSeason)
         .round.greaterEqual(fromRound)
         .teamId(teamId1)
         .rankType("league_id")
       .or
         .season.greaterEqual(fromSeason + 1)
         .teamId(teamId1)
         .rankType("league_id")
       .or
         .season(fromSeason)
         .round.greaterEqual(fromRound)
         .teamId(teamId2)
         .rankType("league_id")
       .or
         .season.greaterEqual(fromSeason + 1)
         .teamId(teamId2)
         .rankType("league_id")

    restClickhouseDAO.execute(builder.sqlWithParameters().build, rowParser)
  }
}
