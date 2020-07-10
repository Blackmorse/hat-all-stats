package com.blackmorse.hattrick.promotions.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Promotion implements IPromotion<PromoteTeam>{
    private final Integer leagueId;
    private final Integer season;
    private final List<PromoteTeam> downTeams;
    private final List<PromoteTeam> upTeams;
}
