package com.blackmorse.hattrick.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamLeague {
    private final Integer leagueId;
    private final Integer leagueLevel;
    private final Integer leagueLevelUnitId;
    private final Integer currentMatchRound;
    private final Long teamId;
    private final String teamName;
}
