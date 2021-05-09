package loadergraph.playerevents

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import chpp.matchdetails.models.{BookingType, InjuryType}
import com.crobox.clickhouse.stream.Insert
import flows.ClickhouseFlow
import models.clickhouse.PlayerEventsModelCH
import models.stream.StreamMatchDetails

import scala.collection.mutable
import scala.concurrent.ExecutionContext

object PlayerEventsFlow {
  def apply(databaseName: String)(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[StreamMatchDetails, Insert, _] = {
    Flow[StreamMatchDetails].flatMapConcat(streamMatchDetails => {
      val playersMap = mutable.Map[Long, PlayerEventsAccumulator]()

      streamMatchDetails.matchDetails.matc.scorers
        .filter(_.scorerTeamId == streamMatchDetails.matc.team.id)
        .foreach(scorer => {
          val playerEvents = playersMap.getOrElseUpdate(scorer.scorerPlayerId,
            playerAccumulator(streamMatchDetails, scorer.scorerPlayerId))

            playerEvents.goals += 1
      })

      streamMatchDetails.matchDetails.matc.injuries
        .filter(_.injuryTeamId == streamMatchDetails.matc.team.id)
        .foreach(injuriedPlayer => {
          val playerEvents = playersMap.getOrElseUpdate(injuriedPlayer.injuryPlayerId,
            playerAccumulator(streamMatchDetails, injuriedPlayer.injuryPlayerId))

          playerEvents.injury = injuriedPlayer.injuryType.id
          if(injuriedPlayer.injuryType == InjuryType.INJURY) {
            playerEvents.leftFieldMinute = injuriedPlayer.injuryMinute
          }
        })

      streamMatchDetails.matchDetails.matc.bookings
        .filter(_.bookingTeamId == streamMatchDetails.matc.team.id)
        .foreach(bookedPlayer => {
          val playerEvents = playersMap.getOrElseUpdate(bookedPlayer.bookingPlayerId,
            playerAccumulator(streamMatchDetails, bookedPlayer.bookingPlayerId))

          if(bookedPlayer.bookingType == BookingType.YELLOW_CARD) {
            playerEvents.yellowCards += 1
          } else {
            playerEvents.yellowCards = 0
            playerEvents.redCards = 1
            playerEvents.leftFieldMinute = bookedPlayer.bookingMinute
          }
        })

      Source(playersMap.values.map(_.build).toList)
    })
      .via(ClickhouseFlow[PlayerEventsModelCH](databaseName, "player_events"))
  }

  private def playerAccumulator(streamMatchDetails: StreamMatchDetails, playerId: Long): PlayerEventsAccumulator =
    PlayerEventsAccumulator(season = streamMatchDetails.matc.season,
      round = streamMatchDetails.matc.round,
      playerId = playerId)
}
