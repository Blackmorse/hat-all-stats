package com.blackmorse.hattrick.promotions.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class Promotion implements IPromotion<PromoteTeam>{
    private final Integer leagueId;
    private final Integer season;
    public Integer upDivisionLevel;
    public PromoteType promoteType;
    private final List<PromoteTeam> downTeams;
    private final List<PromoteTeam> upTeams;
}
