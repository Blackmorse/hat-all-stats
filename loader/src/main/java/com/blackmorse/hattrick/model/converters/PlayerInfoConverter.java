package com.blackmorse.hattrick.model.converters;

import com.blackmorse.hattrick.api.HattrickInfoService;
import com.blackmorse.hattrick.clickhouse.model.PlayerInfo;
import com.blackmorse.hattrick.model.TeamWithMatch;
import com.blackmorse.hattrick.model.TeamWithMatchAndPlayers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class PlayerInfoConverter {
    private final HattrickInfoService hattrickInfoService;

    @Autowired
    public PlayerInfoConverter(HattrickInfoService hattrickInfoService) {
        this.hattrickInfoService = hattrickInfoService;
    }

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
                            .days(player.getAgeDays())
                            .nationality(hattrickInfoService.getCountryIdToLeagueIdMap().get(player.getCountryId()));

                    if(player.getLastMatch().getDate() != null && player.getLastMatch().getDate().equals(teamWithMatch.getMatch().getDate())) {
                        builder.playedMinutes(player.getLastMatch().getPlayedMinutes())
                            .roleId(player.getLastMatch().getPositionCode().getValue())
                            .rating((int)(player.getLastMatch().getRating() * 10))
                            .ratingEndOfMatch((int) (player.getLastMatch().getRatingEndOfGame() * 10));
                    } else {
                        builder.playedMinutes(0)
                                .roleId(0)
                                .rating(0)
                                .ratingEndOfMatch(0);
                    }

                    builder.injuryLevel(player.getInjuryLevel() == null ? -1 : player.getInjuryLevel())
                            .TSI(player.getTsi())
                            .salary(player.getSalary());

                    return builder.build();
                });
    }
}
