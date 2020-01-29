package com.blackmorse.hattrick.model.converters;

import com.blackmorse.hattrick.clickhouse.model.PlayerInfo;
import com.blackmorse.hattrick.model.TeamWithMatch;
import com.blackmorse.hattrick.model.TeamWithMatchAndPlayers;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class PlayerInfoConverter {
    public Stream<PlayerInfo> convert(TeamWithMatchAndPlayers teamWithMatchAndPlayers) {
        TeamWithMatch teamWithMatch = teamWithMatchAndPlayers.getTeamWithMatch();

        return teamWithMatchAndPlayers.getPlayers().getTeam().getPlayerList().stream()
                .map(player -> {
                    PlayerInfo.PlayerInfoBuilder builder = PlayerInfo.builder()
                            .season(teamWithMatch.getMatch().getSeason())
                            .leagueId(teamWithMatch.getTeam().getLeagueUnit().getLeague().getId())
                            .divisionLevel(teamWithMatch.getTeam().getLeagueUnit().getLevel())
                            .leagueUnitId(teamWithMatch.getTeam().getLeagueUnit().getId())
                            .leagueUnitName(teamWithMatch.getTeam().getLeagueUnit().getName())
                            .teamId(teamWithMatch.getTeam().getId())
                            .teamName(teamWithMatch.getTeam().getName())
                            .date(teamWithMatch.getMatch().getDate())
                            .round(teamWithMatch.getMatch().getRound())
                            .matchId(teamWithMatch.getMatch().getId())
                            .playerId(player.getPlayerId())
                            .firstName(player.getFirstName())
                            .lastName(player.getLastName())
                            .age(player.getAge())
                            .days(player.getAgeDays());

                    if(player.getLastMatch().getDate() != null && player.getLastMatch().getDate().equals(teamWithMatch.getMatch().getDate())) {
                        builder.playedMinutes(player.getLastMatch().getPlayedMinutes())
                            .roleId(player.getLastMatch().getPositionCode().getValue())
                            .rating(player.getLastMatch().getRating().intValue())
                            .ratingEndOfMatch(player.getLastMatch().getRatingEndOfGame().intValue());
                    } else {
                        builder.playedMinutes(0)
                                .roleId(0)
                                .rating(0)
                                .ratingEndOfMatch(0);
                    }

                    builder.injuryLevel(player.getInjuryLevel())
                            .TSI(player.getTsi())
                            .salary(player.getSalary());

                    return builder.build();
                });
    }
}
