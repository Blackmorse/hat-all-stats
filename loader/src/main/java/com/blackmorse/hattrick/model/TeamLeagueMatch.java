package com.blackmorse.hattrick.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TeamLeagueMatch {
    private final TeamLeague teamLeague;
    private final Long matchId;
    private final Date date;
}
