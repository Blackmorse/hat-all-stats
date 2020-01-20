package com.blackmorse.hattrick.model;

import com.blackmorse.hattrick.api.leaguedetails.model.LeagueDetails;
import lombok.Data;

@Data
public class LeagueInfoWithLeagueUnitDetails {
    private final LeagueInfo leagueInfo;
    private final LeagueDetails leagueDetails;
}
