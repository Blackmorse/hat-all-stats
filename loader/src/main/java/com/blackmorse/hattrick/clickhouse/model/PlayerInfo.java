package com.blackmorse.hattrick.clickhouse.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PlayerInfo {
    private Integer season;
    private Integer leagueId;
    private Integer divisionLevel;
    private Long leagueUnitId;
    private String leagueUnitName;
    private Long teamId;
    private String teamName;
    private Date date;
    private Integer round;
    private Long matchId;

    private Long playerId;
    private String firstName;
    private String lastName;
    private Integer roleId;
    private Integer playedMinutes;
    private Integer rating;
    private Integer ratingEndOfMatch;
    //0 Bruised, -1 no injury
    private Integer injuryLevel;
    private Integer TSI;
    private Integer salary;
}
