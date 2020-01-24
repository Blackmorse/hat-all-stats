package com.blackmorse.hattrick.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeagueUnitId {
    private final League league;
    private final Long id;
}

