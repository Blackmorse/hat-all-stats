package com.blackmorse.hattrick.model;

import lombok.Data;

@Data
public class LeagueInfoWithLeagueUnitId {
    private final LeagueInfo leagueInfo;
    private final Long leagueUnitId;
}
