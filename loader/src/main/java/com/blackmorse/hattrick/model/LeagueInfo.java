package com.blackmorse.hattrick.model;

import lombok.Data;

import java.util.Date;

@Data
public class LeagueInfo {
    private final Integer leagueId;
    private final Date nextLeagueMatch;
    private final Integer nextRound;
    private final Integer seasonOffset;
}
