package com.blackmorse.hattrick.clickhouse;

import com.blackmorse.hattrick.api.worlddetails.model.League;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TeamRankCalculator {
    private final TeamRankJoiner teamRankJoiner;

    @Autowired
    public TeamRankCalculator(TeamRankJoiner teamRankJoiner) {
        this.teamRankJoiner = teamRankJoiner;
    }

    public void calculate(League league) {
        Integer absoluteSeason = league.getSeason() - league.getSeasonOffset();
        teamRankJoiner.join(absoluteSeason, league.getLeagueId(), league.getMatchRound() - 1, null);
        for (int i = 1; i <= league.getNumberOfLevels(); i++) {
            teamRankJoiner.join(absoluteSeason, league.getLeagueId(), league.getMatchRound() - 1, i);
        }
    }
}
