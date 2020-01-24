package com.blackmorse.hattrick.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeagueUnit {
    private final League league;

    private final Long id;
    private final String name;
    private final Integer level;
}
