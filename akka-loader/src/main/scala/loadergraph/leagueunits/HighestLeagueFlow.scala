package loadergraph.leagueunits

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import chpp.OauthTokens
import models.stream.LeagueUnit

import scala.concurrent.ExecutionContext
import com.blackmorse.hattrick.common.CommonData.higherLeagueMap

object HighestLeagueFlow {
  def apply()(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[LeagueWithLevel, LeagueUnit, NotUsed] = {
    Flow[LeagueWithLevel].map(leagueWithLevel => {
      val leagueUnitInfo = higherLeagueMap.get(leagueWithLevel.league.leagueId)
      LeagueUnit(leagueUnitId = leagueUnitInfo.getLeagueUnitId.toInt,
        leagueUnitName = leagueUnitInfo.getLeagueUnitName,
        level = leagueWithLevel.level,
        league = leagueWithLevel.league)
    })
  }
}
