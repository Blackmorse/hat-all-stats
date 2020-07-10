package com.blackmorse.hattrick.promotions.model;

import java.util.ArrayList;
import java.util.List;

public class DivisionTeams {
    public final Integer divisionLevel;
    public final DivisionDownStrategy downStrategy;
    public List<PromoteTeam> teams = new ArrayList<>();

    public DivisionTeams(Integer divisionLevel, DivisionDownStrategy downStrategy) {
        this.divisionLevel = divisionLevel;
        this.downStrategy = downStrategy;
    }
}
