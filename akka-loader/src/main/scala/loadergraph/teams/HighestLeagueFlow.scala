package loadergraph.teams

import org.apache.pekko.NotUsed
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.Flow
import chpp.OauthTokens
import hattid.CommonData.higherLeagueMap
import models.stream.LeagueUnit

import scala.concurrent.ExecutionContext

object HighestLeagueFlow {
  def apply()(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[LeagueWithLevel, LeagueUnit, NotUsed] = {
    Flow[LeagueWithLevel].map(leagueWithLevel => {
      val leagueUnitInfo = higherLeagueMap(leagueWithLevel.league.leagueId)
      LeagueUnit(leagueUnitId = leagueUnitInfo.leagueUnitId.toInt,
        leagueUnitName = leagueUnitInfo.leagueUnitName,
        level = leagueWithLevel.level,
        league = leagueWithLevel.league)
    })
  }
}
