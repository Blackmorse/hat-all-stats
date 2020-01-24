package com.blackmorse.hattrick.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public
class Team {
    private final LeagueUnit leagueUnit;

    private final Long id;
    private final String name;
}
