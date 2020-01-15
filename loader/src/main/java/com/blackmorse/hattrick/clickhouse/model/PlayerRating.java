package com.blackmorse.hattrick.clickhouse.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PlayerRating {
    private Integer leagueId;
    private Integer divisionLevel;
    private Long leagueUnitId;
    private Long teamId;
    private String teamName;
    private Date date;
    private Integer round;
    private Long matchId;

    private Long playerId;
    private Integer roleId;
    private String firstName;
    private String lastName;
    private Double ratingStars;
    private Double ratingStarsEndOfMatch;
    private Integer behaviour;
}
