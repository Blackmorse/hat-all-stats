package loadergraph.playerevents

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import chpp.matchdetails.models.{BookingType, InjuryType}
import com.crobox.clickhouse.stream.Insert
import loadergraph.ClickhouseFlow
import loadergraph.matchlineup.StreamMatchDetailsWithLineup
import models.clickhouse.PlayerEventsModelCH

import scala.collection.mutable
import scala.concurrent.ExecutionContext

object PlayerEventsFlow {
    def apply(databaseName: String)(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[StreamMatchDetailsWithLineup, Insert, _] = {
    Flow[StreamMatchDetailsWithLineup].flatMapConcat(streamMatchDetails => {
      val playersMap = mutable.Map[Long, PlayerEventsAccumulator]()

      updateGoalsForPlayers(streamMatchDetails, playersMap)
      updateInjuriesForPlayers(streamMatchDetails, playersMap)
      updateCardsForPlayers(streamMatchDetails, playersMap)
      updateSubstitutionTime(streamMatchDetails, playersMap)

      Source(playersMap.values.map(_.build).toList)
    })
      .via(ClickhouseFlow[PlayerEventsModelCH](databaseName, "player_events"))
  }

  private def updateGoalsForPlayers(streamMatchDetails: StreamMatchDetailsWithLineup, playersMap: mutable.Map[Long, PlayerEventsAccumulator]): Unit = {
    streamMatchDetails.matchDetails.matc.scorers
      .filter(_.scorerTeamId == streamMatchDetails.matc.team.id)
      .foreach(scorer => {
        val playerEvents = playersMap.getOrElseUpdate(scorer.scorerPlayerId,
          createPlayerAccumulator(streamMatchDetails, scorer.scorerPlayerId))

        playerEvents.goals += 1
      })
  }

  private def updateInjuriesForPlayers(streamMatchDetails: StreamMatchDetailsWithLineup, playersMap: mutable.Map[Long, PlayerEventsAccumulator]): Unit = {
    streamMatchDetails.matchDetails.matc.injuries
      .filter(_.injuryTeamId == streamMatchDetails.matc.team.id)
      .foreach(injuriedPlayer => {
        val playerEvents = playersMap.getOrElseUpdate(injuriedPlayer.injuryPlayerId,
          createPlayerAccumulator(streamMatchDetails, injuriedPlayer.injuryPlayerId))

        playerEvents.injury = injuriedPlayer.injuryType.id
        if (injuriedPlayer.injuryType == InjuryType.INJURY) {
          playerEvents.leftFieldMinute = injuriedPlayer.injuryMinute
        }
      })
  }

  private def updateCardsForPlayers(streamMatchDetails: StreamMatchDetailsWithLineup, playersMap: mutable.Map[Long, PlayerEventsAccumulator]): Unit = {
    streamMatchDetails.matchDetails.matc.bookings
      .filter(_.bookingTeamId == streamMatchDetails.matc.team.id)
      .foreach(bookedPlayer => {
        val playerEvents = playersMap.getOrElseUpdate(bookedPlayer.bookingPlayerId,
          createPlayerAccumulator(streamMatchDetails, bookedPlayer.bookingPlayerId))

        if (bookedPlayer.bookingType == BookingType.YELLOW_CARD) {
          playerEvents.yellowCards += 1
        } else {
          playerEvents.yellowCards = 0
          playerEvents.redCards = 1
          playerEvents.leftFieldMinute = bookedPlayer.bookingMinute
        }
      })
  }

  private def updateSubstitutionTime(streamMatchDetailsWithLineup: StreamMatchDetailsWithLineup, playersMap: mutable.Map[Long, PlayerEventsAccumulator]): Unit = {
    streamMatchDetailsWithLineup.matchLineup.team.substitutions
      .foreach(substitution => {
        val playerAccumulator = playersMap.getOrElseUpdate(substitution.subjectPlayerId,
          createPlayerAccumulator(streamMatchDetailsWithLineup, substitution.subjectPlayerId))

        playerAccumulator.leftFieldMinute = substitution.matchMinute
      })
  }

  private def createPlayerAccumulator(streamMatchDetails: StreamMatchDetailsWithLineup, playerId: Long): PlayerEventsAccumulator =
    PlayerEventsAccumulator(season = streamMatchDetails.matc.season,
      round = streamMatchDetails.matc.round,
      playerId = playerId)
}
