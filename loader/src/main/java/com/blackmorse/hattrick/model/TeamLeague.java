package com.blackmorse.hattrick.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TeamLeague {
    private final Integer leagueId;
    private final Integer leagueLevel;
    private final Long leagueLevelUnitId;
    private final String leagueUnitName;
    private final Integer nextMatchRound;
    private final Date nextRoundDate;
    private final Long teamId;
    private final String teamName;
    private final Integer seasonOffset;
    //absolute value
    private final Integer currentSeason;
}
