package loadergraph.playerevents

import akka.stream.scaladsl.{Flow, Source}
import chpp.matchdetails.models.{BookingType, InjuryType}
import com.crobox.clickhouse.stream.Insert
import loadergraph.ClickhouseFlow
import loadergraph.matchlineup.StreamMatchDetailsWithLineup
import models.clickhouse.PlayerEventsModelCH

import scala.collection.mutable

object PlayerEventsFlow {
    def apply(databaseName: String): Flow[StreamMatchDetailsWithLineup, Insert, _] = {
    Flow[StreamMatchDetailsWithLineup].flatMapConcat(streamMatchDetails => {
      val playersMap = mutable.Map[Long, PlayerEventsAccumulator]()

      updateGoalsForPlayers(streamMatchDetails, playersMap)
      updateInjuriesForPlayers(streamMatchDetails, playersMap)
      updateCardsForPlayers(streamMatchDetails, playersMap)
      updateSubstitutionTime(streamMatchDetails, playersMap)
      updateStartingLineup(streamMatchDetails, playersMap)

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

  private def updateSubstitutionTime(streamMatchDetails: StreamMatchDetailsWithLineup, playersMap: mutable.Map[Long, PlayerEventsAccumulator]): Unit = {
    streamMatchDetails.matchLineup.team.substitutions
      .foreach(substitution => {
        val playerAccumulator = playersMap.getOrElseUpdate(substitution.subjectPlayerId,
          createPlayerAccumulator(streamMatchDetails, substitution.subjectPlayerId))

        playerAccumulator.leftFieldMinute = substitution.matchMinute
      })
  }

  private def updateStartingLineup(streamMatchDetailsWithLineup: StreamMatchDetailsWithLineup, playersMap: mutable.Map[Long, PlayerEventsAccumulator]): Unit = {
    streamMatchDetailsWithLineup.matchLineup.team.startingLineup
      .filter(s => s.roleId.id >= 100 && s.roleId.id <= 113) // Without captains, subs and set pieces
      .foreach{ startingLineupPlayer =>
        val playerAccumulator = playersMap.getOrElseUpdate(startingLineupPlayer.playerId,
          createPlayerAccumulator(streamMatchDetailsWithLineup, startingLineupPlayer.playerId))

        playerAccumulator.startingLineup = true
      }
  }

  private def createPlayerAccumulator(streamMatchDetails: StreamMatchDetailsWithLineup, playerId: Long): PlayerEventsAccumulator =
    PlayerEventsAccumulator(season = streamMatchDetails.matc.season,
      round = streamMatchDetails.matc.round,
      playerId = playerId)
}
