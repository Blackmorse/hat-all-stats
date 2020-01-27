package com.blackmorse.hattrick.model.converters;

import com.blackmorse.hattrick.api.matchdetails.model.Booking;
import com.blackmorse.hattrick.api.matchdetails.model.Goal;
import com.blackmorse.hattrick.api.matchdetails.model.Injury;
import com.blackmorse.hattrick.clickhouse.model.PlayerEvents;
import com.blackmorse.hattrick.model.TeamWithMatch;
import com.blackmorse.hattrick.model.TeamWithMatchDetails;
import com.blackmorse.hattrick.model.enums.BookingType;
import com.blackmorse.hattrick.model.enums.InjuryType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class PlayerEventsConverter {

    public Stream<PlayerEvents> convert(TeamWithMatchDetails teamWithMatchDetails) {
        TeamWithMatch teamWithMatch = teamWithMatchDetails.getTeamWithMatch();

        Map<Long, PlayerEvents> playersMap = new HashMap<>();

        if (teamWithMatchDetails.getMatchDetails().getMatch().getScorers() != null) {
            for (Goal scorer : teamWithMatchDetails.getMatchDetails().getMatch().getScorers()) {
                if (!scorer.getScorerTeamId().equals(teamWithMatch.getTeam().getId())) continue;

                PlayerEvents playerEvents = playersMap.computeIfAbsent(scorer.getScorerPlayerId(),
                        id -> new PlayerEvents(teamWithMatch.getMatch().getSeason(), teamWithMatch.getMatch().getRound(), scorer.getScorerPlayerId()));
                playerEvents.setGoals(playerEvents.getGoals() + 1);
            }
        }


        if (teamWithMatchDetails.getMatchDetails().getMatch().getInjuries() != null) {
            for (Injury injury : teamWithMatchDetails.getMatchDetails().getMatch().getInjuries()) {
                if (!injury.getInjuryTeamId().equals(teamWithMatch.getTeam().getId())) continue;

                PlayerEvents playerEvents = playersMap.computeIfAbsent(injury.getInjuryPlayerId(),
                        id -> new PlayerEvents(teamWithMatch.getMatch().getSeason(), teamWithMatch.getMatch().getRound(), injury.getInjuryPlayerId()));

                playerEvents.setInjury(injury.getInjuryType().getValue());
                if (injury.getInjuryType().equals(InjuryType.INJURY)) {
                    playerEvents.setLeftFieldMinute(injury.getInjuryMinute());
                }
            }
        }

        if (teamWithMatchDetails.getMatchDetails().getMatch().getBookings() != null) {
            for (Booking booking : teamWithMatchDetails.getMatchDetails().getMatch().getBookings()) {
                if (!booking.getBookingTeamId().equals(teamWithMatch.getTeam().getId())) continue;
                PlayerEvents playerEvents = playersMap.computeIfAbsent(booking.getBookingPlayerId(),
                        id -> new PlayerEvents(teamWithMatch.getMatch().getSeason(), teamWithMatch.getMatch().getRound(), booking.getBookingPlayerId()));

                if (booking.getBookingType().equals(BookingType.YELLOW_CARD)) {
                    playerEvents.setYellowCards(playerEvents.getYellowCards() + 1);
                } else {
                    playerEvents.setYellowCards(0);
                    playerEvents.setRedCards(1);
                    playerEvents.setLeftFieldMinute(booking.getBookingMinute());
                }
            }
        }
        return playersMap.values().stream();
    }
}
