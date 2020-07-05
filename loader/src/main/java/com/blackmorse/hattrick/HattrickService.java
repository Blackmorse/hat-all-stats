package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.LeagueUnitsLoader;
import com.blackmorse.hattrick.api.leaguedetails.model.LeagueDetails;
import com.blackmorse.hattrick.api.worlddetails.model.League;
import com.blackmorse.hattrick.model.TeamWithMatchAndPlayers;
import com.blackmorse.hattrick.model.TeamWithMatchDetails;
import com.blackmorse.hattrick.model.enums.MatchType;
import com.blackmorse.hattrick.model.LeagueUnit;
import com.blackmorse.hattrick.model.Match;
import com.blackmorse.hattrick.model.Team;
import com.blackmorse.hattrick.model.TeamWithMatch;
import com.blackmorse.hattrick.model.TeamWithMatches;
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
public class HattrickService {
    private final Scheduler scheduler;
    private final Hattrick hattrick;
    private final LeagueUnitsLoader leagueUnitsLoader;
    private final AtomicLong leagueUnitCounter = new AtomicLong();
    private final AtomicLong teamsCounter  = new AtomicLong();
    private final AtomicLong matchDetailsCounter  = new AtomicLong();
    private final AtomicLong teamsWithPlayersCounter = new AtomicLong();

    public HattrickService(@Qualifier("apiExecutor") ExecutorService executorService,
                           Hattrick hattrick,
                           LeagueUnitsLoader leagueUnitsLoader) {
        this.scheduler = io.reactivex.schedulers.Schedulers.from(executorService);
        this.hattrick = hattrick;
        this.leagueUnitsLoader = leagueUnitsLoader;
    }

    public List<LeagueUnit> getAllLeagueUnitIdsForCountry(List<String> countryNames) {
        return leagueUnitsLoader.load(countryNames);
    }

    public List<TeamWithMatchDetails> getMatchDetails(List<TeamWithMatches> teamWMatches) {
        return Flowable.fromIterable(teamWMatches)
                .flatMap(teamWithMatches -> Flowable.fromIterable(
                        teamWithMatches.getMatches().stream()
                                .map(match -> TeamWithMatch.builder().team(teamWithMatches.getTeam()).match(match).build())
                                .collect(Collectors.toList())
                ))
                .parallel()
                .runOn(scheduler)
                .map(teamWithMatch -> {
                    com.blackmorse.hattrick.api.matchdetails.model.MatchDetails matchDetails = hattrick.getMatchDetails(teamWithMatch.getMatch().getId());
                    log.debug("Match {}", matchDetailsCounter.incrementAndGet());
                    return TeamWithMatchDetails.builder().teamWithMatch(teamWithMatch).matchDetails(matchDetails).build();
                })
                .sequential()
                .toList()
                .blockingGet();
    }

    public List<TeamWithMatchDetails> getLastMatchDetails(List<LeagueUnit> leagueUnits) {
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

    public List<TeamWithMatchAndPlayers> getPlayersFromTeam(List<TeamWithMatchDetails> teamWithMatchDetails) {
        return Flowable.fromIterable(teamWithMatchDetails)
                .parallel()
                .runOn(scheduler)
                .map(teamWithMatchDetail -> {
                    log.debug("Players for teams: {}", teamsWithPlayersCounter.incrementAndGet());
                    return new TeamWithMatchAndPlayers(teamWithMatchDetail.getTeamWithMatch(),
                            hattrick.getPlayersFromTeam(teamWithMatchDetail.getTeamWithMatch().getTeam().getId()));
                })
                .sequential()
                .toList()
            .blockingGet();
    }

    public League getLeagueByCountryName(String countryName) {
        return hattrick.getWorldDetails().getLeagueList().stream()
                .filter(league -> league.getLeagueName().equals(countryName)).findFirst().get();
    }
}