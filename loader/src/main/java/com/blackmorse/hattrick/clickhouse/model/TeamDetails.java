package com.blackmorse.hattrick.clickhouse.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TeamDetails {
    private final Integer season;
    private final Integer leagueId;
    private final Integer divisionLevel;
    private final Long leagueUnitId;
    private final String leagueUnitName;
    private final Long teamId;
    private final String teamName;
    private final Integer round;

    private final Integer powerRating;
    private final Integer homeFlags;
    private final Integer awayFlags;
    private final Integer fanclubSize;
    private final Integer trophiesNumber;
    private final Integer numberOfVictories;
    private final Integer numberOfUndefeated;
    private final Date foundedDate;
}
