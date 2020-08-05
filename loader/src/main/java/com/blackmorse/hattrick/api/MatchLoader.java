package com.blackmorse.hattrick.api;

import com.blackmorse.hattrick.api.leaguedetails.model.LeagueDetails;
import com.blackmorse.hattrick.model.*;
import com.blackmorse.hattrick.model.enums.MatchType;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MatchLoader {
    private final Scheduler scheduler;
    private final Hattrick hattrick;

    public MatchLoader(@Qualifier("apiExecutor") ExecutorService executorService,
                       Hattrick hattrick) {
        this.scheduler = io.reactivex.schedulers.Schedulers.from(executorService);
        this.hattrick = hattrick;
    }

    public List<TeamWithMatchDetails> getLastMatchDetails(List<LeagueUnit> leagueUnits) {
        AtomicLong leagueUnitCounter = new AtomicLong();
        AtomicLong teamsCounter  = new AtomicLong();
        AtomicLong matchDetailsCounter  = new AtomicLong();

        return Flowable.fromIterable(leagueUnits)
                .parallel()
                .runOn(scheduler)
                .flatMap(leagueUnit -> {
                    LeagueDetails leagueDetails = hattrick.getLeagueUnitById(leagueUnit.getId());

                    log.debug("League details: {}", leagueUnitCounter.incrementAndGet());
                    if (leagueDetails.getTeams() == null) {
                        return Flowable.empty();
                    }
                    return Flowable.fromIterable(leagueDetails.getTeams().stream()
                            .filter(team -> team.getUserId() != 0L)
                            .map(team -> Team.builder()
                                    .leagueUnit(leagueUnit)
                                    .id(team.getTeamId())
                                    .name(team.getTeamName())
                                    .build()).collect(Collectors.toList()));
                })
                .map(team -> {
                    log.debug("teams: {}", teamsCounter.incrementAndGet());
                    List<com.blackmorse.hattrick.api.matchesarchive.model.Match> matchList = hattrick.getCurrentSeasonMatches(team.getId()).getTeam().getMatchList();

                    Match match = null;

                    if(matchList != null) {
                        match = matchList.stream()
                                .filter(m -> m.getMatchType().equals(MatchType.LEAGUE_MATCH))
                                .max(Comparator.comparing(com.blackmorse.hattrick.api.matchesarchive.model.Match::getMatchDate))
                                .map(m -> Match.builder()
                                        .id(m.getMatchId())
                                        .round(team.getLeagueUnit().getLeague().getNextRound() - 1)
                                        .date(m.getMatchDate())
                                        .season(hattrick.getSeason())
                                        .build())
                                .orElse(null);
                    }

                    return TeamWithMatch.builder()
                            .team(team)
                            .match(match)
                            .build();
                })
                .filter(teamWithMatch -> teamWithMatch.getMatch() != null)
                .map(teamWithMatch -> {
                    log.debug("match details: {}", matchDetailsCounter.incrementAndGet());
                    com.blackmorse.hattrick.api.matchdetails.model.MatchDetails matchDetails = hattrick.getMatchDetails(teamWithMatch.getMatch().getId());
                    return TeamWithMatchDetails.builder().teamWithMatch(teamWithMatch).matchDetails(matchDetails).build();
                }).sequential()
                .toList()
                .blockingGet();
    }
}
